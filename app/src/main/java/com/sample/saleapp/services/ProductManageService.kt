package com.sample.saleapp.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.os.Build.VERSION
import com.sample.saleapp.R
import com.sample.saleapp.di.TheApplication
import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.domain.usecases.ProductAddUseCase
import com.sample.saleapp.domain.usecases.ProductSetUpdatedUseCase
import com.sample.saleapp.domain.usecases.ProductUpdateUseCase
import com.sample.saleapp.ui.BottomNavActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProductManageService : Service() {
    @Inject
    lateinit var productSetUpdatedUseCase: ProductSetUpdatedUseCase
    @Inject
    lateinit var productAddUseCase: ProductAddUseCase
    @Inject
    lateinit var productUpdateUseCase: ProductUpdateUseCase

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var beingAddedProductsIds: MutableList<Int>
    private lateinit var beingAddedProductsNames: MutableMap<Int, String>
    private lateinit var beingUpdatedProductsIds: MutableList<Int>
    private lateinit var beingUpdatedProductsNames: MutableMap<Int, String>

    private var foregroundStarted = false
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    companion object {
        private const val DURATION = 5L
        private const val EXTRA_MODE = "MODE"
        private const val EXTRA_ID = "ID"
        private const val EXTRA_NAME = "NAME"
        private const val EXTRA_PRICE = "PRICE"
        private const val EXTRA_QUANTITY = "QUANTITY"
        private const val NOTIFICATION_ID = 113

        fun start(from: Context, isProductUpdated: Boolean, product: Product) {
            from.startService(
                Intent(from, ProductManageService::class.java).apply {
                    putExtra(EXTRA_MODE, isProductUpdated)
                    putExtra(EXTRA_ID, product.id)
                    putExtra(EXTRA_NAME, product.name)
                    putExtra(EXTRA_PRICE, product.price)
                    putExtra(EXTRA_QUANTITY, product.quantity)
                }
            )
        }
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            val isProductUpdated: Boolean = msg.data.getBoolean(EXTRA_MODE)
            val productId: Int = msg.data.getInt(EXTRA_ID)
            val productName: String = msg.data.getString(EXTRA_NAME) ?: "Empty"
            val productPrice: Float = msg.data.getFloat(EXTRA_PRICE)
            val productQuantity: Int = msg.data.getInt(EXTRA_QUANTITY)
            val product = Product(productId, productName, productPrice, productQuantity)

            fun onComplete() {
                if (beingUpdatedProductsIds.isEmpty() && beingAddedProductsIds.isEmpty()) {
                    foregroundStarted = false
                    stopForeground(true)
                } else {
                    updateNotification()
                }
                stopSelf(msg.arg1)
            }

            if (isProductUpdated) {
                productSetUpdatedUseCase.execute(productId)

                compositeDisposable.add(
                    productUpdateUseCase
                        .execute(product)
                        .subscribeOn(Schedulers.io())
                        .delaySubscription(DURATION, TimeUnit.SECONDS)
                        .subscribe {
                            beingUpdatedProductsIds.remove(productId)
                            beingUpdatedProductsNames.remove(productId)
                            onComplete()
                        }
                )
            } else {
                compositeDisposable.add(
                    productAddUseCase
                        .execute(product)
                        .subscribeOn(Schedulers.io())
                        .delaySubscription(DURATION, TimeUnit.SECONDS)
                        .subscribe {
                            beingAddedProductsIds.remove(productId)
                            beingAddedProductsNames.remove(productId)
                            onComplete()
                        }
                )
            }
        }
    }

    override fun onCreate() {
        TheApplication.component.inject(this)

        compositeDisposable = CompositeDisposable()
        beingAddedProductsIds = mutableListOf()
        beingAddedProductsNames = mutableMapOf()
        beingUpdatedProductsIds = mutableListOf()
        beingUpdatedProductsNames = mutableMapOf()

        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val isProductUpdated: Boolean = intent.getBooleanExtra(EXTRA_MODE, false)
        val productId: Int = intent.getIntExtra(EXTRA_ID, -1)
        val productName: String = intent.getStringExtra(EXTRA_NAME)
        val productPrice: Float = intent.getFloatExtra(EXTRA_PRICE, 0.0f)
        val productQuantity: Int = intent.getIntExtra(EXTRA_QUANTITY, 0)

        if (productId == -1) {
            return START_STICKY
        } else {
            if (isProductUpdated) {
                beingUpdatedProductsIds.add(productId)
                beingUpdatedProductsNames[productId] = productName
            } else {
                beingAddedProductsIds.add(productId)
                beingAddedProductsNames[productId] = productName
            }

            tryStartForeground()

            serviceHandler?.obtainMessage()?.also { msg ->
                msg.arg1 = startId
                msg.data = Bundle().apply {
                    putBoolean(EXTRA_MODE, isProductUpdated)
                    putInt(EXTRA_ID, productId)
                    putString(EXTRA_NAME, productName)
                    putFloat(EXTRA_PRICE, productPrice)
                    putInt(EXTRA_QUANTITY, productQuantity)
                }
                serviceHandler?.sendMessage(msg)
            }
        }

        return START_STICKY
    }

    private fun tryStartForeground() {
        if (foregroundStarted) {
            updateNotification()
        } else {
            foregroundStarted = true
            startForeground(NOTIFICATION_ID, getNotification())
        }
    }

    private fun updateNotification() {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID, getNotification())
    }

    private fun getNotification(): Notification {
        val pendingIntent: PendingIntent =
            Intent(this, BottomNavActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notificationBuilder = if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "CHANNEL_MANAGE"
            val channel = NotificationChannel(
                channelId,
                "Product manage notification channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
            Notification.Builder(this, channelId)
        } else {
            Notification.Builder(this)
        }

        return notificationBuilder
            .setContentTitle(getString(R.string.title_manage))
            .setSmallIcon(R.drawable.ic_local_grocery_store)
            .setStyle(Notification.BigTextStyle().bigText(constructNotificationMessage()))
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun constructNotificationMessage(): CharSequence {
        val stringBuilder = StringBuilder()
        beingAddedProductsIds.forEach {
            stringBuilder.append("Adding: ${beingAddedProductsNames[it]} \n")
        }
        beingUpdatedProductsIds.forEach {
            stringBuilder.append("Updating: ${beingUpdatedProductsNames[it]} \n")
        }
        return stringBuilder.toString()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        beingAddedProductsIds.clear()
        beingAddedProductsNames.clear()
        beingUpdatedProductsIds.clear()
        beingUpdatedProductsNames.clear()
        compositeDisposable.clear()
    }
}
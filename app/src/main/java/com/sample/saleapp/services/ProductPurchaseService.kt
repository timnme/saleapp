package com.sample.saleapp.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.os.Build.VERSION
import com.sample.saleapp.R
import com.sample.saleapp.di.TheApplication
import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.domain.usecases.ProductPurchaseUseCase
import com.sample.saleapp.domain.usecases.ProductSetUpdatedUseCase
import com.sample.saleapp.ui.BottomNavActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProductPurchaseService : Service() {
    @Inject
    lateinit var productSetUpdatedUseCase: ProductSetUpdatedUseCase
    @Inject
    lateinit var productPurchaseUseCase: ProductPurchaseUseCase

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var beingPurchasedProductsIds: MutableList<Int>
    private lateinit var beingPurchasedProductsNames: MutableMap<Int, String>

    private var foregroundStarted = false
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    companion object {
        private const val DURATION = 3L
        private const val EXTRA_ID = "ID"
        private const val EXTRA_NAME = "NAME"
        private const val NOTIFICATION_ID = 112

        fun start(from: Context, product: Product) {
            from.startService(
                Intent(from, ProductPurchaseService::class.java).apply {
                    putExtra(EXTRA_ID, product.id)
                    putExtra(EXTRA_NAME, product.name)
                }
            )
        }
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            val productId: Int = msg.arg2
            productSetUpdatedUseCase.execute(productId)
            compositeDisposable.add(
                productPurchaseUseCase
                    .execute(productId)
                    .subscribeOn(Schedulers.io())
                    .delaySubscription(DURATION, TimeUnit.SECONDS)
                    .subscribe {
                        beingPurchasedProductsIds.remove(productId)
                        beingPurchasedProductsNames.remove(productId)
                        if (beingPurchasedProductsIds.isEmpty()) {
                            foregroundStarted = false
                            stopForeground(true)
                        } else {
                            updateNotification()
                        }
                        stopSelf(msg.arg1)
                    })
        }
    }

    override fun onCreate() {
        TheApplication.component.inject(this)

        compositeDisposable = CompositeDisposable()
        beingPurchasedProductsIds = mutableListOf()
        beingPurchasedProductsNames = mutableMapOf()

        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val productId: Int = intent.getIntExtra(EXTRA_ID, -1)
        val productName: String = intent.getStringExtra(EXTRA_NAME)

        if (productId == -1) {
            return START_STICKY
        } else {
            beingPurchasedProductsIds.add(productId)
            beingPurchasedProductsNames[productId] = productName

            tryStartForeground()

            serviceHandler?.obtainMessage()?.also { msg ->
                msg.arg1 = startId
                msg.arg2 = productId
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
            val channelId = "CHANNEL_PURCHASE"
            val channel = NotificationChannel(
                channelId,
                "Product purchase notification channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
            Notification.Builder(this, channelId)
        } else {
            Notification.Builder(this)
        }

        return notificationBuilder
            .setContentTitle("Purchasing products")
            .setSmallIcon(R.drawable.ic_local_grocery_store)
            .setStyle(Notification.BigTextStyle().bigText(constructNotificationMessage()))
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun constructNotificationMessage(): CharSequence {
        val stringBuilder = StringBuilder()
        beingPurchasedProductsIds.forEach {
            stringBuilder.append("Purchasing: ${beingPurchasedProductsNames[it]} \n")
        }
        return stringBuilder.toString()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        beingPurchasedProductsIds.clear()
        beingPurchasedProductsNames.clear()
        compositeDisposable.clear()
    }
}
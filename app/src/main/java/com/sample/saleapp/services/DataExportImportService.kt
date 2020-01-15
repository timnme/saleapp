package com.sample.saleapp.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.widget.Toast
import com.sample.saleapp.di.TheApplication
import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.domain.repositories.ProductRepository
import com.sample.saleapp.utils.DataConverter
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import javax.inject.Inject

class DataExportImportService : Service() {
    @Inject
    lateinit var productRepository: ProductRepository
    @Inject
    lateinit var dataConverter: DataConverter

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    companion object {
        const val FILE_NAME = "store_data_file"

        private const val EXTRA_MODE = "MODE"
        private const val EXTRA_URI = "URI"

        fun start(from: Context, isExport: Boolean, uri: Uri) {
            from.startService(
                Intent(from, DataExportImportService::class.java).apply {
                    putExtra(EXTRA_MODE, isExport)
                    putExtra(EXTRA_URI, uri)
                }
            )
        }
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            val isExport = msg.data.getBoolean(EXTRA_MODE, true)
            msg.data.getParcelable<Uri>(EXTRA_URI)?.let { uri ->
                try {
                    if (isExport) performExport(uri)
                    else performImport(uri)
                } catch (e: Exception) {
                    showError(e)
                    e.printStackTrace()
                } finally {
                    stopSelf(msg.arg1)
                }
            }
        }

        private fun performExport(uri: Uri) {
            val products = productRepository.getAll()
            val export = dataConverter.productsToString(products)
            contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use { outputStream ->
                    outputStream.write(export.toByteArray())
                }
            }
        }

        private fun performImport(uri: Uri) {
            val stringBuilder = StringBuilder()
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line)
                        line = reader.readLine()
                    }
                }
            }
            val import = stringBuilder.toString()
            val products: List<Product> = dataConverter.stringToProducts(import)
            productRepository.add(products)
        }

        private fun showError(e: Exception) {
            object : HandlerThread("UiHandlerThread") {
                override fun run() {
                    Handler(Looper.getMainLooper()) {
                        Toast.makeText(
                            this@DataExportImportService,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }.sendEmptyMessage(0)
                }
            }.start()
        }
    }

    override fun onCreate() {
        TheApplication.component.inject(this)

        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val isExport = intent.getBooleanExtra(EXTRA_MODE, true)
        intent.getParcelableExtra<Uri>(EXTRA_URI)?.let {
            serviceHandler?.obtainMessage()?.also { msg ->
                msg.arg1 = startId
                msg.data.putBoolean(EXTRA_MODE, isExport)
                msg.data.putParcelable(EXTRA_URI, it)
                serviceHandler?.sendMessage(msg)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null
}
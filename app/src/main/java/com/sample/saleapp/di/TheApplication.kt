package com.sample.saleapp.di

import android.app.Application
import com.sample.saleapp.di.modules.ApplicationModule

class TheApplication : Application() {
    companion object {
        lateinit var component: ApplicationComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }
}
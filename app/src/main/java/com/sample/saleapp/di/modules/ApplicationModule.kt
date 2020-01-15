package com.sample.saleapp.di.modules

import android.content.Context
import com.sample.saleapp.di.ForApplication
import com.sample.saleapp.di.TheApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: TheApplication) {
    @Provides
    @Singleton
    fun provideApplication(): TheApplication {
        return application
    }

    @Provides
    @Singleton
    @ForApplication
    fun provideApplicationContext(): Context {
        return application.applicationContext
    }
}
package com.sample.saleapp.di.modules

import com.sample.saleapp.utils.DataConverter
import com.sample.saleapp.utils.JsonDataConverter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilsModule {
    @Provides
    @Singleton
    fun provideDataConverter(): DataConverter {
        return JsonDataConverter()
    }
}
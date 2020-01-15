package com.sample.saleapp.di.modules

import com.sample.saleapp.data.database.ProductDao
import com.sample.saleapp.data.modelconverter.DataModelConverter
import com.sample.saleapp.data.modelconverter.DataModelConverterImpl
import com.sample.saleapp.data.repositories.ProductRepositoryImpl
import com.sample.saleapp.domain.repositories.ProductRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {
    @Provides
    @Singleton
    fun provideDataModelConverter(): DataModelConverter {
        return DataModelConverterImpl()
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        dataModelConverter: DataModelConverter,
        dao: ProductDao
    ): ProductRepository {
        return ProductRepositoryImpl(dataModelConverter, dao)
    }
}
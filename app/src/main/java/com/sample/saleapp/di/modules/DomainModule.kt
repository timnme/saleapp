package com.sample.saleapp.di.modules

import com.sample.saleapp.domain.repositories.ProductRepository
import com.sample.saleapp.domain.usecases.*
import dagger.Module
import dagger.Provides

@Module
class DomainModule {
    @Provides
    fun provideProductGetAllUnsoldUseCase(
        productRepository: ProductRepository
    ): ProductGetAllUnsoldUseCase {
        return ProductGetAllUnsoldUseCaseImpl(productRepository)
    }

    @Provides
    fun provideProductGetAllUseCase(
        productRepository: ProductRepository
    ): ProductGetAllUseCase {
        return ProductGetAllUseCaseImpl(productRepository)
    }

    @Provides
    fun provideProductGetByIdUseCase(
        productRepository: ProductRepository
    ): ProductGetByIdUseCase {
        return ProductGetByIdUseCaseImpl(productRepository)
    }

    @Provides
    fun provideProductAddUseCase(
        productRepository: ProductRepository
    ): ProductAddUseCase {
        return ProductAddUseCaseImpl(productRepository)
    }


    @Provides
    fun provideProductUpdateUseCase(
        productRepository: ProductRepository
    ): ProductUpdateUseCase {
        return ProductUpdateUseCaseImpl(productRepository)
    }

    @Provides
    fun provideProductBeingUpdatedUseCase(
        productRepository: ProductRepository
    ): ProductSetUpdatedUseCase {
        return ProductSetUpdatedUseCaseImpl(productRepository)
    }

    @Provides
    fun provideProductPurchaseUseCase(
        productRepository: ProductRepository
    ): ProductPurchaseUseCase {
        return ProductPurchaseUseCaseImpl(productRepository)
    }
}
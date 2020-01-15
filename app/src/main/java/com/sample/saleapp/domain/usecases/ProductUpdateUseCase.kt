package com.sample.saleapp.domain.usecases

import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.domain.repositories.ProductRepository
import io.reactivex.Completable

interface ProductUpdateUseCase {
    fun execute(product: Product): Completable
}

class ProductUpdateUseCaseImpl(
    private val repository: ProductRepository
) : ProductUpdateUseCase {
    override fun execute(product: Product): Completable {
        return repository.updateCompletable(product.copy(isBeingUpdated = false))
    }
}
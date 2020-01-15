package com.sample.saleapp.domain.usecases

import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.domain.repositories.ProductRepository
import io.reactivex.Completable

interface ProductAddUseCase {
    fun execute(product: Product): Completable
}

class ProductAddUseCaseImpl(
    private val repository: ProductRepository
) : ProductAddUseCase {
    override fun execute(product: Product): Completable {
        return repository.addCompletable(product)
    }
}
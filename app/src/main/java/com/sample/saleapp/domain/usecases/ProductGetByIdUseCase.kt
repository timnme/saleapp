package com.sample.saleapp.domain.usecases

import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.domain.repositories.ProductRepository
import io.reactivex.Single

interface ProductGetByIdUseCase {
    fun execute(productId: Int): Single<Product>
}

class ProductGetByIdUseCaseImpl(
    private val repository: ProductRepository
) : ProductGetByIdUseCase {
    override fun execute(productId: Int): Single<Product> {
        return repository.getByIdSingle(productId)
    }
}
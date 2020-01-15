package com.sample.saleapp.domain.usecases

import com.sample.saleapp.domain.repositories.ProductRepository

/**
 * Sets a product in the update state
 */
interface ProductSetUpdatedUseCase {
    fun execute(id: Int)
}

class ProductSetUpdatedUseCaseImpl(
    private val repository: ProductRepository
) : ProductSetUpdatedUseCase {
    override fun execute(id: Int) {
        val product = repository.getById(id)
        repository.update(product.copy(isBeingUpdated = true))
    }
}
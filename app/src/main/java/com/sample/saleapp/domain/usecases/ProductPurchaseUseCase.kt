package com.sample.saleapp.domain.usecases

import com.sample.saleapp.domain.repositories.ProductRepository
import io.reactivex.Completable

interface ProductPurchaseUseCase {
    fun execute(id: Int): Completable
}

class ProductPurchaseUseCaseImpl(
    private val repository: ProductRepository
) : ProductPurchaseUseCase {
    override fun execute(id: Int): Completable {
        return repository.getByIdSingle(id).flatMapCompletable { product ->
            // if the product quantity is grater than zero, decrement it
            if (product.quantity >= 1) {
                repository.updateCompletable(
                    product.copy(quantity = product.quantity - 1, isBeingUpdated = false)
                )
            } else {
                repository.updateCompletable(product.copy(isBeingUpdated = false))
            }
        }
    }
}
package com.sample.saleapp.domain.usecases

import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.domain.repositories.ProductRepository
import io.reactivex.Observable

interface ProductGetAllUnsoldUseCase {
    fun execute(): Observable<List<Product>>
}

class ProductGetAllUnsoldUseCaseImpl(
    private val repository: ProductRepository
) : ProductGetAllUnsoldUseCase {
    override fun execute(): Observable<List<Product>> {
        return repository.getAllObservable().map { products ->
            // return only those products which quantity is grater than zero
            products.filter { it.quantity > 0 }
        }
    }
}
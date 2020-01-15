package com.sample.saleapp.domain.usecases

import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.domain.repositories.ProductRepository
import io.reactivex.Observable

interface ProductGetAllUseCase {
    fun execute(): Observable<List<Product>>
}

class ProductGetAllUseCaseImpl(
    private val repository: ProductRepository
) : ProductGetAllUseCase {
    override fun execute(): Observable<List<Product>> {
        return repository.getAllObservable()
    }
}
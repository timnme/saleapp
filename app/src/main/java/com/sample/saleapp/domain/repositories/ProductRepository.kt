package com.sample.saleapp.domain.repositories

import com.sample.saleapp.domain.models.Product
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Provides data for the domain layer
 */
interface ProductRepository {
    fun addCompletable(product: Product): Completable
    fun getAllObservable(): Observable<List<Product>>
    fun getAllSingle(): Single<List<Product>>
    fun getByIdSingle(id: Int): Single<Product>
    fun updateCompletable(product: Product): Completable

    fun add(product: Product)
    fun add(products: List<Product>)
    fun getById(id: Int): Product
    fun getAll(): List<Product>
    fun update(product: Product)
}

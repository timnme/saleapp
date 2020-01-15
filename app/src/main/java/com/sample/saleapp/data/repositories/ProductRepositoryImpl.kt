package com.sample.saleapp.data.repositories

import com.sample.saleapp.data.database.ProductDao
import com.sample.saleapp.data.modelconverter.DataModelConverter
import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.domain.repositories.ProductRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class ProductRepositoryImpl(
    private val converter: DataModelConverter,
    private val dao: ProductDao
) : ProductRepository {
    override fun addCompletable(product: Product): Completable {
        return dao.insertCompletable(converter.toDb(product))
    }

    override fun getAllObservable(): Observable<List<Product>> {
        return dao.getAllObservable().map(converter::toDomain)
    }

    override fun getAllSingle(): Single<List<Product>> {
        return dao.getAllSingle().map(converter::toDomain)
    }

    override fun getByIdSingle(id: Int): Single<Product> {
        return dao.getByIdSingle(id).map(converter::toDomain)
    }

    override fun updateCompletable(product: Product): Completable {
        return dao.updateCompletable(converter.toDb(product))
    }


    override fun add(product: Product) {
        return dao.insert(converter.toDb(product))
    }

    override fun add(products: List<Product>) {
        return dao.insert(converter.toDb(products))
    }

    override fun getById(id: Int): Product {
        return converter.toDomain(dao.getById(id))
    }

    override fun getAll(): List<Product> {
        return dao.getAll().map(converter::toDomain)
    }

    override fun update(product: Product) {
        return dao.update(converter.toDb(product))
    }
}
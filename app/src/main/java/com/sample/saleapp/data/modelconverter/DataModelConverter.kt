package com.sample.saleapp.data.modelconverter

import com.sample.saleapp.data.database.models.DbProduct
import com.sample.saleapp.domain.models.Product

/**
 * Interface for classes providing data conversion from the data layer to the domain layer
 */
interface DataModelConverter {
    fun toDomain(product: DbProduct): Product
    fun toDomain(products: List<DbProduct>): List<Product>
    fun toDb(product: Product): DbProduct
    fun toDb(products: List<Product>): List<DbProduct>
}

class DataModelConverterImpl : DataModelConverter {
    override fun toDomain(product: DbProduct): Product {
        return with(product) {
            Product(
                id = id,
                name = name,
                price = price,
                quantity = quantity,
                isBeingUpdated = isUpdated
            )
        }
    }

    override fun toDomain(products: List<DbProduct>): List<Product> {
        return products.map(::toDomain)
    }

    override fun toDb(product: Product): DbProduct {
        return with(product) {
            DbProduct(
                id = id,
                name = name,
                price = price,
                quantity = quantity,
                isUpdated = isBeingUpdated
            )
        }
    }

    override fun toDb(products: List<Product>): List<DbProduct> {
        return products.map(::toDb)
    }
}
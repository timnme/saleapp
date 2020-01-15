package com.sample.saleapp.data.database

import androidx.room.*
import com.sample.saleapp.data.database.models.DbProduct
import com.sample.saleapp.domain.models.Product
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompletable(product: DbProduct): Completable

    @Query("SELECT * FROM products")
    fun getAllObservable(): Observable<List<DbProduct>>

    @Query("SELECT * FROM products")
    fun getAllSingle(): Single<List<DbProduct>>

    @Query("SELECT * FROM products WHERE id=:id")
    fun getByIdSingle(id: Int): Single<DbProduct>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCompletable(product: DbProduct): Completable


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(product: DbProduct)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(products: List<DbProduct>)

    @Query("SELECT * FROM products")
    fun getAll(): List<DbProduct>

    @Query("SELECT * FROM products WHERE id=:id")
    fun getById(id: Int): DbProduct

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(product: DbProduct)

    @Query("DELETE FROM products")
    fun deleteAll()
}
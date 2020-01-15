package com.sample.saleapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sample.saleapp.data.database.models.DbProduct
import java.util.concurrent.Executors

@Database(entities = [DbProduct::class], version = 1, exportSchema = false)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        private const val DATABASE_NAME = "com.sample.saleapp.database"

        @Volatile
        private var INSTANCE: ApplicationDatabase? = null

        fun getDatabase(context: Context): ApplicationDatabase = INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context, ApplicationDatabase::class.java, DATABASE_NAME
            )
                .addCallback(ProductDatabaseCallback())
                .build()

            INSTANCE = instance

            instance
        }
    }

    private class ProductDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // Prepopulate the database
            INSTANCE?.let { database ->
                Executors.newCachedThreadPool().execute {
                    val dao: ProductDao = database.productDao()
                    dao.deleteAll()
                    dao.insert(listOf(
                        DbProduct(name = "Apple iPod touch 5 32Gb", price = 8888f, quantity = 5),
                        DbProduct(name = "Samsung Galaxy S Duos S7562", price = 7230f, quantity = 2),
                        DbProduct(name = "Canon EOS 600D Kit", price = 15659f, quantity = 4),
                        DbProduct(name = "Samsung Galaxy Tab 2 10.1 P5100 16Gb", price = 13290f, quantity = 9),
                        DbProduct(name = "PocketBook Touch", price = 5197f, quantity = 2),
                        DbProduct(name = "Samsung Galaxy Note II 16Gb", price = 17049.50f, quantity = 2),
                        DbProduct(name = "Nikon D3100 Kit", price = 12190f, quantity = 4),
                        DbProduct(name = "Canon EOS 1100D Kit", price = 10985f, quantity = 2),
                        DbProduct(name = "Sony Xperia Acro S", price = 11800.99f, quantity = 1),
                        DbProduct(name = "Lenovo G580", price = 8922f, quantity = 1)
                    ))
                }
            }
        }
    }

}
package com.sample.saleapp.di.modules

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sample.saleapp.data.database.ApplicationDatabase
import com.sample.saleapp.data.database.ProductDao
import com.sample.saleapp.di.ForApplication
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ForApplication context: Context): ApplicationDatabase {
        return ApplicationDatabase.getDatabase(context)
    }


    @Provides
    @Singleton
    fun provideProductDao(database: ApplicationDatabase): ProductDao {
        return database.productDao()
    }
}
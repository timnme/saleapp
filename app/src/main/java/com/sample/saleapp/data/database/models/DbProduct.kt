package com.sample.saleapp.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class DbProduct(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Float,
    val quantity: Int,
    val isUpdated: Boolean = false
)
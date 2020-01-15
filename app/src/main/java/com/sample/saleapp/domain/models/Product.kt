package com.sample.saleapp.domain.models

data class Product(
    val id: Int,
    val name: String,
    val price: Float,
    val quantity: Int,
    val isBeingUpdated: Boolean = false
)
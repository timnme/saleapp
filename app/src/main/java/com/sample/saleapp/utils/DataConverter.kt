package com.sample.saleapp.utils

import com.sample.saleapp.domain.models.Product
import org.json.JSONArray
import org.json.JSONObject

/**
 * Converts the app data to store it in a given form
 */
interface DataConverter {
    fun productsToString(products: List<Product>): String
    fun stringToProducts(string: String): List<Product>
}

class JsonDataConverter : DataConverter {
    override fun productsToString(products: List<Product>): String {
        val jsonArray = JSONArray()
        products.forEach {
            val jsonObject = JSONObject()
            jsonObject.put("id", it.id)
            jsonObject.put("name", it.name)
            jsonObject.put("price", it.price)
            jsonObject.put("quantity", it.quantity)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    override fun stringToProducts(string: String): List<Product> {
        val jsonArray = JSONArray(string)
        val products: MutableList<Product> = mutableListOf()
        for (i in 0 until jsonArray.length()) {
            val jsonProduct = jsonArray.getJSONObject(i)
            val product = Product(
                id = 0,
                name = jsonProduct.getString("name"),
                price = jsonProduct.getDouble("price").toFloat(),
                quantity = jsonProduct.getInt("quantity")
            )
            products.add(product)
        }
        return products
    }
}
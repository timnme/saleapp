package com.sample.saleapp.ui.storefront

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sample.saleapp.R
import com.sample.saleapp.domain.models.Product

class ProductsCarouselAdapter(
    var products: List<Product> = emptyList(),
    private val onPurchaseClick: (product: Product) -> Unit
) : RecyclerView.Adapter<ProductsCarouselAdapter.ProductViewHolder>() {
    override fun getItemCount(): Int = products.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_product_carousel, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val productNameTextView: TextView = itemView.findViewById(R.id.name)
        private val productPriceTextView: TextView = itemView.findViewById(R.id.price)
        private val productQuantityTextView: TextView = itemView.findViewById(R.id.quantity)
        private val productPurchaseButton: Button = itemView.findViewById(R.id.purchase)

        fun bind(product: Product) {
            productNameTextView.text = product.name
            productPriceTextView.text = String.format(
                itemView.context.getString(R.string.text_product_price),
                product.price.toString()
            )
            productQuantityTextView.text = String.format(
                itemView.context.getString(R.string.text_product_quantity),
                product.quantity.toString()
            )
            if (product.isBeingUpdated) {
                productPurchaseButton.isEnabled = false
                productPurchaseButton.text =
                    itemView.context.getString(R.string.msg_product_being_updated)
            } else {
                productPurchaseButton.isEnabled = true
                productPurchaseButton.text = itemView.context.getString(R.string.title_purchase)
                productPurchaseButton.setOnClickListener { onPurchaseClick(product) }
            }
        }
    }
}
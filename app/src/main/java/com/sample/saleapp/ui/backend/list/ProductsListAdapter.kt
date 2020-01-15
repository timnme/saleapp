package com.sample.saleapp.ui.backend.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sample.saleapp.R
import com.sample.saleapp.domain.models.Product

class ProductsListAdapter(
    var products: List<Product> = emptyList(),
    private val onProductClick: (id: Int) -> Unit
) : RecyclerView.Adapter<ProductsListAdapter.ProductViewHolder>() {

    override fun getItemCount(): Int = products.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_product_list, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val productLayout: ViewGroup = itemView.findViewById(R.id.productLayout)
        private val productNameTextView: TextView = itemView.findViewById(R.id.productName)
        private val productQuantityTextView: TextView = itemView.findViewById(R.id.productQuantity)
        private val stubView: TextView = itemView.findViewById(R.id.stub)

        fun bind(product: Product) {
            productNameTextView.text = product.name
            productQuantityTextView.text = String.format(
                itemView.context.getString(R.string.text_product_quantity),
                product.quantity.toString()
            )
            if (product.isBeingUpdated) {
                productLayout.setOnClickListener(null)
                stubView.visibility = View.VISIBLE
            } else {
                productLayout.setOnClickListener { onProductClick(product.id) }
                stubView.visibility = View.GONE
            }
        }
    }
}
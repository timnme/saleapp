package com.sample.saleapp.ui.storefront

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.sample.saleapp.R
import com.sample.saleapp.di.TheApplication
import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.mvp.MvpFragment
import com.sample.saleapp.utils.ext.toast
import kotlinx.android.synthetic.main.fragment_storefront.*
import javax.inject.Inject

/**
 * Fragment for showing the products list like carousel
 * Here the user can purchase a product
 */
class StoreFrontFragment : MvpFragment<StoreFrontContract.View, StoreFrontContract.Presenter>(),
    StoreFrontContract.View {
    @Inject
    override lateinit var presenter: StoreFrontContract.Presenter
    private lateinit var adapter: ProductsCarouselAdapter
    private lateinit var listener: OnProductPurchaseListener

    interface OnProductPurchaseListener {
        /**
         * The user hits the purchase button
         */
        fun onProductPurchase(product: Product)
    }

    companion object {
        fun newInstance(): StoreFrontFragment = StoreFrontFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnProductPurchaseListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$context must implement ${OnProductPurchaseListener::class.java.simpleName}"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TheApplication.component.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.fragment_storefront, container, false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductsCarouselAdapter(onPurchaseClick = listener::onProductPurchase)
        pager_ViewPager.adapter = adapter

        presenter.loadProducts()
    }

    override fun onProductsLoadSuccess(products: List<Product>) {
        adapter.products = products
        adapter.notifyDataSetChanged()
    }

    override fun onProductsLoadError(errMsg: String?) {
        toast(errMsg)
    }
}
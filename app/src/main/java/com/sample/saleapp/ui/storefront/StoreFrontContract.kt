package com.sample.saleapp.ui.storefront

import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.mvp.BaseMvpPresenter
import com.sample.saleapp.mvp.MvpView

interface StoreFrontContract {
    interface View : MvpView {
        fun onProductsLoadSuccess(products: List<Product>)
        fun onProductsLoadError(errMsg: String?)
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun loadProducts()
    }
}
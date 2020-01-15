package com.sample.saleapp.ui.backend.manage

import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.mvp.BaseMvpPresenter
import com.sample.saleapp.mvp.MvpView

interface BackendManageContract {
    interface View : MvpView {
        fun onProductLoadSuccess(product: Product)
        fun onProductLoadError(errMsg: String?)
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun loadProduct(id: Int)
    }
}
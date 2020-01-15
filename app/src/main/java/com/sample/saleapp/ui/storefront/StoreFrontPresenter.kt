package com.sample.saleapp.ui.storefront

import com.sample.saleapp.domain.usecases.ProductGetAllUnsoldUseCase

class StoreFrontPresenter(
    private val productGetAllUnsoldUseCase: ProductGetAllUnsoldUseCase
) : StoreFrontContract.Presenter() {
    override fun loadProducts() {
        productGetAllUnsoldUseCase
            .execute()
            .registerAndListen(
                onNext = { view.onProductsLoadSuccess(it) },
                onError = { view.onProductsLoadError(it.message) }
            )
    }
}
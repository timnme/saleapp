package com.sample.saleapp.ui.backend.list

import com.sample.saleapp.domain.usecases.ProductGetAllUseCase

class BackendListPresenter(
    private val productGetAllUseCase: ProductGetAllUseCase
) : BackendListContract.Presenter() {
    override fun loadProducts() {
        productGetAllUseCase
            .execute()
            .registerAndListen(
                onNext = { view.onProductsLoadSuccess(it) },
                onError = { view.onProductsLoadError(it.message) }
            )
    }
}
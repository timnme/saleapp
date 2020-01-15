package com.sample.saleapp.ui.backend.manage

import com.sample.saleapp.domain.usecases.ProductGetByIdUseCase

class BackendManagePresenter(
    private val productGetByIdUseCase: ProductGetByIdUseCase
) : BackendManageContract.Presenter() {
    override fun loadProduct(id: Int) {
        productGetByIdUseCase
            .execute(id)
            .registerAndListen(
                onSuccess = { view.onProductLoadSuccess(it) },
                onError = { view.onProductLoadError(it.message) }
            )
    }
}
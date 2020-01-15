package com.sample.saleapp.di.modules

import com.sample.saleapp.domain.usecases.*
import com.sample.saleapp.ui.backend.list.BackendListContract
import com.sample.saleapp.ui.backend.list.BackendListPresenter
import com.sample.saleapp.ui.backend.manage.BackendManageContract
import com.sample.saleapp.ui.backend.manage.BackendManagePresenter
import com.sample.saleapp.ui.storefront.StoreFrontContract
import com.sample.saleapp.ui.storefront.StoreFrontPresenter
import dagger.Module
import dagger.Provides

@Module
class UiModule {
    @Provides
    fun provideStoreFrontPresenter(
        productGetAllUnsoldUseCase: ProductGetAllUnsoldUseCase
    ): StoreFrontContract.Presenter {
        return StoreFrontPresenter(
            productGetAllUnsoldUseCase = productGetAllUnsoldUseCase
        )
    }

    @Provides
    fun provideBackendListPresenter(
        productGetAllUseCase: ProductGetAllUseCase
    ): BackendListContract.Presenter {
        return BackendListPresenter(
            productGetAllUseCase = productGetAllUseCase
        )
    }

    @Provides
    fun provideBackendManagePresenter(
        productGetByIdUseCase: ProductGetByIdUseCase
    ): BackendManageContract.Presenter {
        return BackendManagePresenter(
            productGetByIdUseCase = productGetByIdUseCase
        )
    }
}
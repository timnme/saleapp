package com.sample.saleapp.di

import com.sample.saleapp.di.modules.*
import com.sample.saleapp.services.DataExportImportService
import com.sample.saleapp.services.ProductManageService
import com.sample.saleapp.services.ProductPurchaseService
import com.sample.saleapp.ui.backend.list.BackendListFragment
import com.sample.saleapp.ui.backend.manage.BackendManageFragment
import com.sample.saleapp.ui.storefront.StoreFrontFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        DatabaseModule::class,
        DataModule::class,
        DomainModule::class,
        UiModule::class,
        UtilsModule::class
    ]
)
interface ApplicationComponent {
    fun inject(fragment: StoreFrontFragment)
    fun inject(fragment: BackendListFragment)
    fun inject(fragment: BackendManageFragment)
    fun inject(service: ProductPurchaseService)
    fun inject(service: ProductManageService)
    fun inject(service: DataExportImportService)
}
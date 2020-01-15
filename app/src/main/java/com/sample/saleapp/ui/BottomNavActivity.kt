package com.sample.saleapp.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.sample.saleapp.R
import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.services.DataExportImportService
import com.sample.saleapp.services.ProductManageService
import com.sample.saleapp.services.ProductPurchaseService
import com.sample.saleapp.ui.backend.list.BackendListFragment
import com.sample.saleapp.ui.backend.manage.BackendManageFragment
import com.sample.saleapp.ui.storefront.StoreFrontFragment
import kotlinx.android.synthetic.main.activity_bottom_nav.*

class BottomNavActivity : AppCompatActivity(),
    StoreFrontFragment.OnProductPurchaseListener,
    BackendListFragment.OnDataExportImportListener,
    BackendListFragment.OnProductManageListener,
    BackendManageFragment.OnProductManageListener {

    private val storeFrontFragment: StoreFrontFragment by lazy {
        StoreFrontFragment.newInstance()
    }

    private val backendListFragment: BackendListFragment by lazy {
        BackendListFragment.newInstance()
    }

    private companion object {
        const val CREATE_FILE = 1
        const val OPEN_FILE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        nav_BottomNavView.setOnNavigationItemSelectedListener {
            loadFragment(
                when (it.itemId) {
                    R.id.store_front -> storeFrontFragment
                    R.id.back_end -> backendListFragment
                    else -> null
                }
            )
        }
        nav_BottomNavView.selectedItemId = R.id.store_front
    }

    /**
     * Helper method to load fragments
     */
    private fun loadFragment(
        fragment: Fragment?, addToBackStack: Boolean = false, name: String? = null
    ): Boolean = if (fragment != null) {
        val transaction: FragmentTransaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) transaction.addToBackStack(name)
        transaction.commit()
        true
    } else false

    override fun onProductPurchase(product: Product) {
        ProductPurchaseService.start(this, product)
    }

    override fun onExportClick() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, DataExportImportService.FILE_NAME)
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    override fun onImportClick() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, OPEN_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                when (requestCode) {
                    // Start export service
                    CREATE_FILE -> DataExportImportService.start(this, true, it)
                    // Start import service
                    OPEN_FILE -> DataExportImportService.start(this, false, it)
                    else -> Unit
                }
            }
        }
    }

    override fun onProductAddClick() {
        loadFragment(
            BackendManageFragment.newInstance(false), true
        )
    }

    override fun onProductUpdateClick(productId: Int) {
        loadFragment(
            BackendManageFragment.newInstance(true, productId), true
        )
    }

    override fun onProductManage(isProductUpdated: Boolean, product: Product) {
        ProductManageService.start(this, isProductUpdated, product)
    }

    override fun onProductManageFinish() {
        supportFragmentManager.popBackStack()
    }
}
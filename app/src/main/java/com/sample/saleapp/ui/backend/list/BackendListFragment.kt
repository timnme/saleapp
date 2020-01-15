package com.sample.saleapp.ui.backend.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sample.saleapp.R
import com.sample.saleapp.di.TheApplication
import com.sample.saleapp.domain.models.Product
import com.sample.saleapp.mvp.MvpFragment
import com.sample.saleapp.utils.ext.toast
import kotlinx.android.synthetic.main.fragment_backend_list.*
import javax.inject.Inject

/**
 * Fragment to show the products list
 * Here the user can proceed with either adding a new product or editing an existing one
 * Also here he can export/import the data into/from the desired location
 */
class BackendListFragment : MvpFragment<BackendListContract.View, BackendListContract.Presenter>(),
    BackendListContract.View {
    @Inject
    override lateinit var presenter: BackendListContract.Presenter
    private lateinit var dataManageListener: OnDataExportImportListener
    private lateinit var productManageListener: OnProductManageListener
    private lateinit var adapter: ProductsListAdapter

    interface OnDataExportImportListener {
        fun onExportClick()
        fun onImportClick()
    }

    interface OnProductManageListener {
        fun onProductAddClick()
        fun onProductUpdateClick(productId: Int)
    }

    companion object {
        fun newInstance(): BackendListFragment = BackendListFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataManageListener = context as OnDataExportImportListener
            productManageListener = context as OnProductManageListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$context must implement ${OnProductManageListener::class.java.simpleName} " +
                        "and ${OnDataExportImportListener::class.java.simpleName}"
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
        R.layout.fragment_backend_list, container, false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductsListAdapter {
            productManageListener.onProductUpdateClick(it)
        }
        products_RecyclerView.adapter = adapter
        setOnClickListeners()

        presenter.loadProducts()
    }

    private fun setOnClickListeners() {
        add_Button.setOnClickListener {
            productManageListener.onProductAddClick()
        }
        export_Button.setOnClickListener {
            dataManageListener.onExportClick()
        }
        import_Button.setOnClickListener {
            dataManageListener.onImportClick()
        }
    }

    override fun onProductsLoadSuccess(products: List<Product>) {
        adapter.products = products
        adapter.notifyDataSetChanged()
    }

    override fun onProductsLoadError(errMsg: String?) {
        toast(errMsg)
    }
}

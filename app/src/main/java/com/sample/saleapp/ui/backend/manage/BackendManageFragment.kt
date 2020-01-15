package com.sample.saleapp.ui.backend.manage

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
import kotlinx.android.synthetic.main.fragment_backend_manage.*
import javax.inject.Inject

/**
 * Fragment where the user can edit a product or add a new one
 */
class BackendManageFragment :
    MvpFragment<BackendManageContract.View, BackendManageContract.Presenter>(),
    BackendManageContract.View {
    @Inject
    override lateinit var presenter: BackendManageContract.Presenter
    private lateinit var listener: OnProductManageListener

    interface OnProductManageListener {
        fun onProductManage(isProductUpdated: Boolean, product: Product)
        fun onProductManageFinish()
    }

    companion object {
        private const val MANAGE_MODE = "MANAGE_MODE"
        private const val PRODUCT_ID = "ID"

        fun newInstance(
            isManageModeUpdate: Boolean, productId: Int? = null
        ): BackendManageFragment = BackendManageFragment().apply {
            arguments = Bundle().apply {
                putBoolean(MANAGE_MODE, isManageModeUpdate)
                if (isManageModeUpdate) try {
                    putInt(PRODUCT_ID, productId!!)
                } catch (e: NullPointerException) {
                    throw NullPointerException(
                        "Product id must not be null when manage mode is UPDATE"
                    )
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnProductManageListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$context must implement ${OnProductManageListener::class.java.simpleName}"
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
        R.layout.fragment_backend_manage, container, false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isManageModeUpdate: Boolean = arguments?.getBoolean(MANAGE_MODE) ?: false
        val productId: Int? = arguments?.getInt(PRODUCT_ID)

        if (isManageModeUpdate) productId?.let(presenter::loadProduct)

        setOnClickListeners(isManageModeUpdate, productId)
    }

    private fun setOnClickListeners(isManageModeUpdate: Boolean, productId: Int?) {
        cancel_Button.setOnClickListener {
            listener.onProductManageFinish()
        }

        save_Button.setOnClickListener {
            // Retrieving the entered data, validating the data
            val name: String = with(name_EditText.text.toString()) {
                if (isBlank()) {
                    toast("Wrong name")
                    return@setOnClickListener
                } else this
            }
            val price: Float = try {
                price_EditText.text.toString().toFloat()
            } catch (e: NumberFormatException) {
                toast("Wrong price")
                return@setOnClickListener
            }
            val quantity: Int = try {
                quantity_EditText.text.toString().toInt()
            } catch (e: NumberFormatException) {
                toast("Wrong quantity")
                return@setOnClickListener
            }

            val product = Product(
                id = productId ?: 0,
                name = name,
                price = price,
                quantity = quantity
            )

            listener.onProductManage(isManageModeUpdate, product)
            listener.onProductManageFinish()
        }
    }

    override fun onProductLoadSuccess(product: Product) {
        name_EditText.text.insert(0, product.name)
        price_EditText.text.insert(0, product.price.toString())
        quantity_EditText.text.insert(0, product.quantity.toString())
    }

    override fun onProductLoadError(errMsg: String?) {
        toast(errMsg)
    }
}

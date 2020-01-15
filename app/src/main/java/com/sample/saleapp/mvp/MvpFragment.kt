package com.sample.saleapp.mvp

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment

/**
 * Base class for fragments to implement MVP pattern
 */
abstract class MvpFragment<V : MvpView, P : MvpPresenter<V>> : Fragment() {
    protected abstract val presenter: P

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            presenter.onViewAttached(this as V)
        } catch (e: ClassCastException) {
            throw ClassCastException("$this must implement MvpView")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onViewDetached()
    }
}
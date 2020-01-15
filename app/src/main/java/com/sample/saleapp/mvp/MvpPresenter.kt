package com.sample.saleapp.mvp

interface MvpPresenter<V : MvpView> {
    fun onViewAttached(view: V)
    fun onViewDetached()
}
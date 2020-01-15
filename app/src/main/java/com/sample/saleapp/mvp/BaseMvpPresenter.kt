package com.sample.saleapp.mvp

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class BaseMvpPresenter<V : MvpView> : MvpPresenter<V> {
    private val referencesHolder = ResettableReferences()

    private lateinit var compositeDisposable: CompositeDisposable
    protected var view: V by Resettable(referencesHolder)

    override fun onViewAttached(view: V) {
        this.view = view
        compositeDisposable = CompositeDisposable()
    }

    override fun onViewDetached() {
        referencesHolder.reset()
        compositeDisposable.clear()
    }

    protected fun <T> Observable<T>.listen(
        onNext: (T) -> Unit, onError: (Throwable) -> Unit
    ): Disposable = this
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext { log(it) }
        .doOnError { log(it) }
        .subscribe(onNext::invoke, onError::invoke)

    protected fun <T> Single<T>.listen(
        onSuccess: (T) -> Unit, onError: (Throwable) -> Unit
    ): Disposable = this
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { log(it) }
        .doOnError { log(it) }
        .subscribe(onSuccess::invoke, onError::invoke)

    protected fun Completable.listen(
        onComplete: () -> Unit, onError: (Throwable) -> Unit
    ): Disposable = this
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnComplete { log("Completed") }
        .doOnError { log(it) }
        .subscribe(onComplete::invoke, onError::invoke)

    protected fun <T> Observable<T>.registerAndListen(
        onNext: (T) -> Unit, onError: (Throwable) -> Unit
    ) {
        listen(onNext, onError).register()
    }

    protected fun <T> Single<T>.registerAndListen(
        onSuccess: (T) -> Unit, onError: (Throwable) -> Unit
    ) {
        listen(onSuccess, onError).register()
    }

    protected fun Completable.registerAndListen(
        onComplete: () -> Unit, onError: (Throwable) -> Unit
    ) {
        listen(onComplete, onError).register()
    }

    private companion object {
        const val TAG = "APP_LOGS"
    }

    private fun <T> log(t: T) = Log.d(TAG, t.toString())
    private fun log(t: Throwable) = Log.e(TAG, "Error", t)

    private fun Disposable.register() {
        compositeDisposable.add(this)
    }
}
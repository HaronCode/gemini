package com.haroncode.gemini.android

import androidx.lifecycle.LifecycleOwner
import com.haroncode.gemini.android.binder.MainThreadConnectionBinder
import com.haroncode.gemini.android.lifecycle.AndroidStoreLifecycle
import com.haroncode.gemini.android.strategies.StartStopStrategy
import com.haroncode.gemini.binder.BaseConnectionBinder
import io.reactivex.disposables.Disposable

object AndroidBinder {

    fun <T : LifecycleOwner> withBinding(viewBinding: ViewBinding<T>) = Builder(viewBinding)

    class Builder<T : LifecycleOwner>(private val viewBinding: ViewBinding<T>) {

        private var bindingStrategy: BindingStrategy = StartStopStrategy

        fun withStrategy(bindingStrategy: BindingStrategy): Builder<T> {
            this.bindingStrategy = bindingStrategy
            return this
        }

        fun create(view: T): Disposable {
            val lifecycle = AndroidStoreLifecycle(
                view.lifecycle,
                bindingStrategy
            )

            val baseConnectionBinder = BaseConnectionBinder(lifecycle)
            val mainThreadConnectionBinder =
                MainThreadConnectionBinder(baseConnectionBinder)

            viewBinding.onCreate(view, mainThreadConnectionBinder)
            return mainThreadConnectionBinder
        }
    }
}
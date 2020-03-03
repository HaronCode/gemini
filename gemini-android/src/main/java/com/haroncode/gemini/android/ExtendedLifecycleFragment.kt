package com.haroncode.gemini.android

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle

open class ExtendedLifecycleFragment(
    @LayoutRes contentLayoutId: Int = 0
) : Fragment(contentLayoutId) {

    private var instanceStateSaved: Boolean = false

    private val realRemovingObservers = mutableSetOf<ExtendedLifecycleObserver>()

    private val extendedLifecycle = ExtendedLifecycle(
        lifecycle = super.getLifecycle(),
        realRemovingObserversHolder = object : RealRemovingObserversHolder {
            override fun add(observer: ExtendedLifecycleObserver) {
                realRemovingObservers.add(observer)
            }

            override fun remove(observer: ExtendedLifecycleObserver) {
                realRemovingObservers.remove(observer)
            }
        }
    )

    override fun getLifecycle(): Lifecycle = extendedLifecycle

    override fun onStart() {
        super.onStart()
        instanceStateSaved = false
    }

    override fun onResume() {
        super.onResume()
        instanceStateSaved = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        instanceStateSaved = true
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()

        val isFinishing = activity?.isFinishing == true || isRealRemoving

        if (isFinishing) realRemovingObservers.forEach(ExtendedLifecycleObserver::onFinish)
    }

    private val isRealRemoving: Boolean
        get() {
            // When we rotate device isRemoving() return true for fragment placed in backstack
            // http://stackoverflow.com/questions/34649126/fragment-back-stack-and-isremoving
            if (instanceStateSaved) {
                instanceStateSaved = false
                return false
            }
            return isRemoving || isAnyParentRemoving
        }

    private val isAnyParentRemoving: Boolean
        get() {
            var isAnyParentRemoving = false
            var parent = parentFragment
            while (!isAnyParentRemoving && parent != null) {
                isAnyParentRemoving = parent.isRemoving
                parent = parent.parentFragment
            }
            return isAnyParentRemoving
        }
}

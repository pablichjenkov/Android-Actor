package com.hamperapp

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class RenderContextDefault(
    private val fragmentManager: FragmentManager,
    private val viewContainer: ViewGroup
) : RenderContext {

    @IdRes
    private val viewContainerId: Int = viewContainer.id


    override fun <F : Fragment> setView(fragment: F, fragmentId: String) {

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(viewContainerId, fragment, fragmentId)
        fragmentTransaction.commitNow()

    }

    override fun <V : View> setView(view: V) {
        viewContainer.removeAllViews()
        viewContainer.addView(view)
    }

}
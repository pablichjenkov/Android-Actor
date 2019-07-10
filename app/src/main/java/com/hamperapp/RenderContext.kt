package com.hamperapp

import android.view.View
import androidx.fragment.app.Fragment

interface RenderContext {

    fun <F : Fragment> setView(fragment: F, fragmentId: String)

    fun <V : View> setView(view: V)

}
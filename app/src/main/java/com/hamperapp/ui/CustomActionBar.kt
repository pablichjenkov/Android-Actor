package com.hamperapp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.hamperapp.R
import kotlinx.android.synthetic.main.custom_action_bar.view.*

class CustomActionBar @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

	var onLeftIconTap: (() -> Unit)? = null

	var onRightIconTap: (() -> Unit)? = null


	init {

		LayoutInflater.from(context).inflate(R.layout.custom_action_bar, this, true)

	}

	override fun onFinishInflate() {
		super.onFinishInflate()

		leftIcon.setOnClickListener {

			onLeftIconTap?.invoke()

		}

		rightIcon.setOnClickListener {

			onRightIconTap?.invoke()

		}

	}

	fun setObserver(dsl: CustomActionBar.() -> Unit) {

		dsl()

	}

	fun setTitle(title: String) {

		centerText.text = title

	}

}
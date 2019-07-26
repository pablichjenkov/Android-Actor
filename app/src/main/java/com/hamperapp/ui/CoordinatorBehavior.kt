package com.hamperapp.ui

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.view.animation.LinearInterpolator
import androidx.annotation.NonNull
import androidx.core.os.HandlerCompat.postDelayed
import com.hamperapp.log.Logger


class FabBehaviour : CoordinatorLayout.Behavior<FloatingActionButton> {
	internal var mHandler: Handler? = null

	constructor(context: Context, attrs: AttributeSet) : super() {}


	override fun onStopNestedScroll(
		@NonNull coordinatorLayout: CoordinatorLayout,
		@NonNull child: FloatingActionButton,
		@NonNull target: View,
		type: Int
	) {
		super.onStopNestedScroll(coordinatorLayout, child, target, type)
		if (mHandler == null)
			mHandler = Handler()


		mHandler!!.postDelayed(Runnable {
			child.animate().translationY(0f).setInterpolator(LinearInterpolator()).start()
			Logger.d("FabAnim: startHandler()")
		}, 1000)
	}

	override fun onNestedScroll(
		@NonNull coordinatorLayout: CoordinatorLayout,
		@NonNull child: FloatingActionButton,
		@NonNull target: View,
		dxConsumed: Int,
		dyConsumed: Int,
		dxUnconsumed: Int,
		dyUnconsumed: Int,
		type: Int
	) {
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)

		if (dyConsumed > 0) {
			Logger.d("Scrolling: Up")
			val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
			val fab_bottomMargin = layoutParams.bottomMargin
			child.animate().translationY((child.height + fab_bottomMargin).toFloat())
				.setInterpolator(LinearInterpolator()).start()

		} else if (dyConsumed < 0) {
			Logger.d("Scrolling: down")
			child.animate().translationY(0f).setInterpolator(LinearInterpolator()).start()
		}

	}

	override fun onStartNestedScroll(
		@NonNull coordinatorLayout: CoordinatorLayout,
		@NonNull child: FloatingActionButton,
		@NonNull directTargetChild: View,
		@NonNull target: View,
		axes: Int,
		type: Int
	): Boolean {

		if (mHandler != null) {
			mHandler!!.removeMessages(0)
			Logger.d("Scrolling: stopHandler()")
		}

		return axes == ViewCompat.SCROLL_AXIS_VERTICAL

	}


	companion object {
		private val TAG = "ScrollingFABBehavior"
	}


}
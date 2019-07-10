package com.hamperapp.log

import android.util.Log

object Logger {

    val TAG: String = "HamperApplication"

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun e(throwable: Throwable) {

        throwable.message?.let {
            Log.e(TAG, it)
        }

        // TODO(Pablo): Log this to a remote error collector endpoint
    }

}
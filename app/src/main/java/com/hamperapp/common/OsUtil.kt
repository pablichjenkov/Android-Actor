package com.hamperapp.common

import android.content.Context
import android.provider.Settings



object OsUtil {

    fun deviceId(context: Context): String = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )



}

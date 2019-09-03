package com.hamperapp.common

interface AppLifecycleListener {
    fun onAppOpened()
    fun onAppGotoForeground()
    fun onAppGotoBackground()
    fun onAppClosed()
}
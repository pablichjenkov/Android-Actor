package com.hamperapp

import android.app.Application
import com.hamperapp.auth.AuthManager


class HamperApplication : Application() {

    lateinit var authManager: AuthManager


    override fun onCreate() {
        super.onCreate()

        instance = this@HamperApplication

        authManager = AuthManager

    }

    companion object {

        lateinit var instance: HamperApplication

    }

}

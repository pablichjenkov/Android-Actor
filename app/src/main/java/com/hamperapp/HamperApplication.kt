package com.hamperapp

import android.app.Application
import com.hamperapp.auth.AuthManager
import com.hamperapp.common.ConfigManager


class HamperApplication : Application() {

    lateinit var authManager: AuthManager

    lateinit var configManager: ConfigManager


    override fun onCreate() {
        super.onCreate()

        instance = this@HamperApplication

        authManager = AuthManager

        configManager = ConfigManager(this@HamperApplication)

    }

    companion object {

        lateinit var instance: HamperApplication

    }

}

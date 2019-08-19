package com.hamperapp

import android.app.Application
import com.hamperapp.auth.AuthManager
import com.hamperapp.common.ConfigManager


class HamperApplication : Application() {

    lateinit var authManager: AuthManager

    lateinit var configManager: ConfigManager

    lateinit var storageManager: StorageManager


    override fun onCreate() {
        super.onCreate()

        instance = this@HamperApplication

        configManager = ConfigManager(this@HamperApplication)

        storageManager = StorageManager(this@HamperApplication)

        authManager = AuthManager(storageManager)

    }

    companion object {

        lateinit var instance: HamperApplication

    }

}

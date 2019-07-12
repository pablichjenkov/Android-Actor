package com.hamperapp

import android.app.Application
import com.hamperapp.auth.AuthAPI
import com.hamperapp.auth.AuthActor
import retrofit2.Retrofit


class HamperApplication : Application() {

    lateinit var authActor: AuthActor

    lateinit var authAPI: AuthAPI


    override fun onCreate() {
        super.onCreate()

        instance = this@HamperApplication

        authActor = AuthActor()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .build()

        authAPI = retrofit.create<AuthAPI>(AuthAPI::class.java)

    }

    companion object {

        lateinit var instance: HamperApplication

    }

}

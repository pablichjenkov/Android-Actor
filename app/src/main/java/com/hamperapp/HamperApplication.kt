package com.hamperapp

import android.app.Application
import com.hamperapp.auth.AuthAPI
import com.hamperapp.auth.AuthActor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import retrofit2.Retrofit



class HamperApplication : Application() {

    lateinit var appCoroutineScope: CoroutineScope

    lateinit var authActor: AuthActor

    lateinit var authAPI: AuthAPI


    override fun onCreate() {
        super.onCreate()

        instance = this@HamperApplication

        appCoroutineScope = CoroutineScope(Dispatchers.IO)

        val sendChannel = appCoroutineScope.actor<Any> {

            consumeEach { action ->
                when (action) {
                }
            }

        }

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

package com.hamperapp.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthAPI {

    @POST("authenticate")
    fun authenticate(@Body authReqReq: AuthMsg.AuthReq): Call<String>

}
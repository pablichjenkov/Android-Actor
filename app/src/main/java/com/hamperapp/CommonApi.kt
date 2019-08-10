package com.hamperapp

import com.google.gson.annotations.SerializedName
import com.hamperapp.network.http.Http
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface CommonApi {

	@POST("register")
	@Headers(
		Http.Header_ContentType_ApplicationJson,
		Http.Header_Accept_ApplicationJson
	)
	fun signup(@Body signupReq: SignupReq): Call<SignUpResp>

	@POST("login")
	@Headers(
		Http.Header_ContentType_ApplicationJson,
		Http.Header_Accept_ApplicationJson
	)
	fun login(@Body emailLogin: EmailLoginReq): Call<LoginResp>

	@POST("login")
	@Headers(
		Http.Header_ContentType_ApplicationJson,
		Http.Header_Accept_ApplicationJson
	)
	fun login(@Body emailLogin: PhoneLoginReq): Call<LoginResp>

	@POST("login")
	@Headers(
		Http.Header_ContentType_ApplicationJson,
		Http.Header_Accept_ApplicationJson
	)
	fun login(@Body emailLogin: UsernameLoginReq): Call<LoginResp>

}

//********************************************************************************************************************//
//**************************************************** signup ********************************************************//
//********************************************************************************************************************//

data class SignupReq (
	@SerializedName("email") val email : String?,
	@SerializedName("password") val password : String?,
	@SerializedName("phone") val phone : String?,
	@SerializedName("zip") val zipcode : String?,
	@SerializedName("name") val name : String?,
	@SerializedName("device_token") val deviceToken : String?,
	@SerializedName("device_id") val deviceId : String?,
	@SerializedName("request_id") val requestId : String? // request_id from /api/verify-phone/check response
)

data class AccessToken(
	val token: String?,
	val expires: Long?
)

data class User(
	val email: String?,
	val password: String?,
	val phone: String?,
	val zip: String?,
	val name: String?
)

data class SignUpResp(
	@SerializedName("access_token") val accessToken: AccessToken?,
	val user: User?,
	val zip: String?
)

//********************************************************************************************************************//
//**************************************************** login *********************************************************//
//********************************************************************************************************************//

abstract class LoginReq

data class EmailLoginReq(
	val email: String,
	val password: String,
	@SerializedName("device_token") val deviceToken: String,
	@SerializedName("device_id") val deviceId: String
) : LoginReq()

data class PhoneLoginReq(
	val phone: String,
	val password: String,
	@SerializedName("device_token") val deviceToken: String,
	@SerializedName("device_id") val deviceId: String
) : LoginReq()

data class UsernameLoginReq(
	val username: String,
	val password: String,
	@SerializedName("device_token") val deviceToken: String,
	@SerializedName("device_id") val deviceId: String
) : LoginReq()

data class LoginResp(
	val token: String?,
	val role: String?,
	val userId: String?
)

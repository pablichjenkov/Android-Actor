package com.hamperapp

import com.google.gson.annotations.SerializedName
import com.hamperapp.network.http.Http
import retrofit2.Call
import retrofit2.http.*


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

	@GET("zip-code/{zipCode}/check")
	@Headers(
		Http.Header_ContentType_ApplicationJson,
		Http.Header_Accept_ApplicationJson
	)
	fun checkZipCode(
		@Header(Http.Header_Authorization) authToken: String,
		@Path("zipCode") zipCode: String
	): Call<ZipcodeCheckResp>

}


//********************************************************************************************************************//
//**************************************************** signup ********************************************************//
//********************************************************************************************************************//

data class SignupReq (
	@SerializedName("email") val email : String?,
	@SerializedName("password") val password : String?,
	@SerializedName("phone") val phone : String?,
	@SerializedName("country_code") val countryCode : String?,
	@SerializedName("zip") val zipcode : String?,
	@SerializedName("name") val name : String?
	//@SerializedName("device_token") val deviceToken : String?,
	//@SerializedName("device_id") val deviceId : String?,
	//@SerializedName("request_id") val requestId : String? // request_id from /api/verify-phone/check response
)

data class HamperAccessToken(
	val token: String?,
	val expires: Long?
)

data class SignupUser(
	val email: String?,
	val password: String?,
	val phone: String?,
	val zip: String?,
	val name: String?
)

data class SignUpResp(
	@SerializedName("access_token") val accessToken: HamperAccessToken?,
	@SerializedName("user") val signupUser: SignupUser?,
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
	@SerializedName("user") val loginUser: LoginUser?,
	val userId: Int?,
	val role: String?,
	val token: String?
)

data class LoginUser(
	val id: Int?,
	val name: String?,
	val email: String?,
	val phone: String?,
	val city: String?,
	val address: String?,
	@SerializedName("subscription_id") val subscriptionId: Long?,
	@SerializedName("total_spent") val totalSpent: String?,
	val laundry: Int?,
	@SerializedName("dry_cleaning") val dryCleaning: Int?,
	@SerializedName("created_at") val createdAt: String?,
	@SerializedName("updated_at") val updatedAt: String?,
	@SerializedName("remember_token") val rememberToken: String?,
	@SerializedName("zip_id") val zipId: Int?,
	val status: String?,
	@SerializedName("payment_streak") val paymentStreak: Int?,
	@SerializedName("stripe_id") val stripeId: String?,
	@SerializedName("phone_verified") val phoneVerified: Boolean?,
	@SerializedName("sms_notification") val smsNotification: Boolean?,
	@SerializedName("push_notification") val pushNotification: Boolean?,
	@SerializedName("email_notification") val emailNotification: Boolean?,
	@SerializedName("country_code") val countryCode: String?
)

//********************************************************************************************************************//
//**************************************************** zipcode *******************************************************//
//********************************************************************************************************************//

data class ZipcodeCheckResp(
	val checked: Boolean = false
)

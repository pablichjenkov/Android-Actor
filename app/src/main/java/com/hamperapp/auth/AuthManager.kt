package com.hamperapp.auth

import com.facebook.AccessToken
import com.hamperapp.*
import com.hamperapp.common.OsUtil
import com.hamperapp.network.http.Http
import com.hamperapp.network.http.asFlow
import kotlinx.coroutines.flow.*
import retrofit2.Call


class AuthManager(val storageManager: StorageManager) {

	private val commonApi = Http.provideCommonApi

	private val signupFormHolder = SignupForm()

	private var signUpRespSnapshot: SignUpResp? = null

	private val loginFormHolder = LoginForm()

	private var loginRespSnapshot: LoginResp? = null


	fun doLogin(loginReq: LoginReq): Flow<LoginResp> {

		lateinit var call: Call<LoginResp>

		when (loginReq) {

			is EmailLoginReq -> {
				call = commonApi.login(loginReq)
			}

			is PhoneLoginReq -> {
				call = commonApi.login(loginReq)
			}

			is UsernameLoginReq -> {
				call = commonApi.login(loginReq)
			}

		}

		return call
			.asFlow()
			.onEach {
				loginRespSnapshot = it
			}

	}

	fun verifyFBLogin(): Boolean {

		val accessToken = AccessToken.getCurrentAccessToken()

		return accessToken != null && !accessToken.isExpired

	}

	fun doLoginWithFacebookToken(): Flow<Boolean> {

		val accessToken = AccessToken.getCurrentAccessToken()

		val fbLogin = accessToken != null && !accessToken.isExpired

		if (! fbLogin) {

			return flowOf(false)

		}

		val deviceId = OsUtil.deviceId(storageManager.appContext)

		val loginWithFacebookReq = LoginWithFacebookReq(
			accessToken.token,
			"not_ready",
			deviceId)

		return commonApi
			.loginWithFacebookToken(loginWithFacebookReq)
			.asFlow()
			.onEach {

				loginRespSnapshot = it.toLoginResp()

			}.map {
				true
			}

	}

	fun doSignup(signupReq: SignupReq): Flow<SignUpResp> = commonApi
		.signup(signupReq)
		.asFlow()

	fun authToken(): String? {

		return loginRespSnapshot?.token

	}

	fun checkZipCode(zipCode: String): Flow<String> = flow {

		//doRequest(commonApi.signup(signupReq))

	}

	fun persistZipCode(zipCode: String) {

		return storageManager.write("KEY_zipCode", zipCode)

	}

	fun zipCode(): String? {

		return storageManager.read("KEY_zipCode")

	}

}
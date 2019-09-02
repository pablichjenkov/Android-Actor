package com.hamperapp.auth

import com.hamperapp.*
import com.hamperapp.network.http.Http
import com.hamperapp.network.http.asFlow
import com.hamperapp.network.http.doRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import retrofit2.Call


class AuthManager(val storageManager: StorageManager) {

	private val commonApi = Http.provideCommonApi

	private val signupFormHolder = SignupForm()

	private var signUpRespSnapshot: SignUpResp? = null

	private val loginFormHolder = LoginForm()

	private var loginRespSnapshot: LoginResp? = null


	fun doLogin(loginReq: LoginReq): Flow<LoginResp> = flow<LoginResp> {

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

		doRequest(call)

	}.onEach {

		loginRespSnapshot = it

	}

	fun doSignup(signupReq: SignupReq): Flow<SignUpResp> = commonApi.signup(signupReq).asFlow()

	fun checkZipCode(zipCode: String): Flow<String> = flow {

		//doRequest(commonApi.signup(signupReq))

	}

	fun authToken(): String = loginRespSnapshot?.token.orEmpty()

	fun persistZipCode(zipCode: String) {

		return storageManager.write("KEY_zipCode", zipCode)

	}

	fun zipCode(): String? {

		return storageManager.read("KEY_zipCode")

	}

}
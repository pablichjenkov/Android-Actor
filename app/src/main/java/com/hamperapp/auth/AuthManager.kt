package com.hamperapp.auth

import com.hamperapp.*
import com.hamperapp.network.http.Http
import com.hamperapp.network.http.doRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call


object AuthManager {

	private val commonApi = Http.provideCommonApi


	fun doLogin(loginReq: LoginReq): Flow<LoginResp> = flow {

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

	}

	fun doSignup(signupReq: SignupReq): Flow<SignUpResp> = flow {

		doRequest(commonApi.signup(signupReq))

	}

}
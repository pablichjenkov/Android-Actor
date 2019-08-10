package com.hamperapp.network.http

import kotlinx.coroutines.flow.FlowCollector
import retrofit2.Call


suspend fun <Resp> FlowCollector<Resp>.doRequest(call: Call<Resp>) {

	val resp = call.execute()

	if (resp.isSuccessful) {

		when( val respBody = resp.body() ) {

			null -> { throw HttpNullBodyException() }

			else -> {

				emit(respBody)

			}

		}

	} else {

		val errorInfo = resp.errorBody()?.string() ?: "errorBody empty"

		throw HttpCodeException(resp.code(), errorInfo)

	}

}
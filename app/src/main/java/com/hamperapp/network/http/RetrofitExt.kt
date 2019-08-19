package com.hamperapp.network.http

import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
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

fun <T> Call<T>.asFlow(): Flow<T> = flow {

	val resp = execute()

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

inline fun <reified T> genericType() = object: TypeToken<T>(){}.type

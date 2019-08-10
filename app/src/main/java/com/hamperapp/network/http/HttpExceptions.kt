package com.hamperapp.network.http

class HttpCodeException(val code: Int, message: String) : Exception(message)

class HttpNullBodyException : Exception()

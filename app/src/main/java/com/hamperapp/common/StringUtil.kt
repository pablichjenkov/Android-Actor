package com.hamperapp.common

import android.text.TextUtils
import android.util.Patterns


object StringUtil {


	fun isValidEmail(email: String): Boolean {

		return (!TextUtils.isEmpty(email)
				&& Patterns.EMAIL_ADDRESS.matcher(email).matches())

	}

	fun isValidPhone(phoneNumber: String): Boolean {
		//TODO: Implement phone validation using google phonelib for Android
		return true

	}

	fun isValidPassword(password: String): Boolean {
		return password.length >= 8

	}

}
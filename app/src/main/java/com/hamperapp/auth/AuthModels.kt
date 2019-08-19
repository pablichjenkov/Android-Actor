package com.hamperapp.auth


class SignupForm(
	var email : String? = null,
	var password : String? = null,
	var phone : String? = null,
	var countryCode : String? = null,
	var zipcode : String? = null,
	var firstname : String? = null,
	var lastname : String? = null,
	var termsAgree: Boolean = false
) {

	val name : String
		get() { return "$firstname $lastname" }

}

class LoginForm(
	var usernameOrPhoneNumber: String? = null,
	var password: String? = null
)
package com.hamperapp.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hamperapp.R
import com.hamperapp.SignupReq
import com.hamperapp.common.StringUtil
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class SignupFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var actor: AuthPresenterActor


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signupBtn.setOnClickListener {

            validateFormAndDoRequest()

        }

    }

    override fun onStart() {
        super.onStart()

        actor.fragmentChannel = fragmentCoroutineScope.actor {

            consumeEach { event: Any ->

                when (event) {

                    AuthPresenterActor.OutMsg.View.Signup.OnLoad -> {

                        progressBar.visibility = View.VISIBLE

                    }

                    AuthPresenterActor.OutMsg.View.Signup.OnSuccess -> {

                        progressBar.visibility = View.GONE

                        Toast.makeText(context, "Sign up Successful", Toast.LENGTH_SHORT).show()

                    }

                    is AuthPresenterActor.OutMsg.View.Signup.OnError -> {

                        progressBar.visibility = View.GONE

                        Toast.makeText(context, event.th.message, Toast.LENGTH_SHORT).show()

                    }

                }

            }

        }

        actor.send(AuthPresenterActor.InMsg.View.OnLoginViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(AuthPresenterActor.InMsg.View.OnSignupViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()
    }

    fun validateFormAndDoRequest() {

        var errorCount = 0

        val emailText = email.text.toString()

        if (!StringUtil.isValidEmail(emailText)) {

            presentErrorText(invalidEmailLabel)

            errorCount ++

        }

        val phoneNumber = phone.text.toString()

        if (!StringUtil.isValidPhone(phoneNumber)) {

            presentErrorText(invalidPhoneLabel)

            errorCount ++

        }

        val passwordText = password.text.toString()

        if (!StringUtil.isValidPassword(passwordText)) {

            presentErrorText(invalidPasswordLabel)

            errorCount ++

        }

        if (errorCount > 0) {
            return
        }

        val signupReq = SignupReq(
            email = emailText,
            password = passwordText,
            phone = phoneNumber,
            zipcode = "33182",
            name = firstName.text.toString(),
            deviceToken = "crap_for_now",// TODO: Get this from the Push Notification provider
            deviceId = "emulator",
            requestId = "crap_for_now" // TODO: Get this from the Phone verifier provider
        )

        actor.send(AuthPresenterActor.InMsg.View.DoSignUp(signupReq))

    }

    private fun presentErrorText(textView: TextView) {

        fragmentCoroutineScope.launch {

            textView.visibility = View.VISIBLE

            delay(5000)

            textView.visibility = View.INVISIBLE

        }

    }


    companion object {

        @JvmStatic
        fun newInstance(actor: AuthPresenterActor) = SignupFragment().apply { this.actor = actor }

    }

}

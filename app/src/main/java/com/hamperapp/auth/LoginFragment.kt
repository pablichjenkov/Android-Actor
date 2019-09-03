package com.hamperapp.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hamperapp.EmailLoginReq
import com.hamperapp.R
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import com.facebook.CallbackManager
import com.facebook.login.LoginManager


class LoginFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var actor: AuthPresenterActor

    private var fbCallbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fbCallbackManager = CallbackManager.Factory.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginBtn.setOnClickListener {

            val loginReq = EmailLoginReq(
                email = emailOrPhone.text.toString(),
                password = password.text.toString(),
                deviceToken = "crap_for_now",
                deviceId = "emulator"
            )

            actor.send(AuthPresenterActor.InMsg.View.DoLogin(loginReq))

        }

        fbLoginBtn.setOnClickListener {

            LoginManager.getInstance().logInWithReadPermissions(this, listOf(FB_EMAIL))

        }

    }

    override fun onStart() {
        super.onStart()

        actor.fragmentChannel = fragmentCoroutineScope.actor {

            consumeEach { event: Any ->

                when (event) {

                    AuthPresenterActor.OutMsg.View.Login.OnLoad -> {

                        progressBar.visibility = View.VISIBLE

                    }

                    AuthPresenterActor.OutMsg.View.Login.OnSuccess -> {

                        progressBar.visibility = View.GONE

                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()

                    }

                    is AuthPresenterActor.OutMsg.View.Login.OnError -> {

                        progressBar.visibility = View.GONE

                        Toast.makeText(context, event.th.message, Toast.LENGTH_SHORT).show()

                    }

                }

            }

        }

        actor.send(AuthPresenterActor.InMsg.View.OnLoginViewReady)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        fbCallbackManager?.onActivityResult(requestCode, resultCode, data)

        actor.send(AuthPresenterActor.InMsg.View.OnLoginFragmentResult)
    }

    override fun onStop() {
        super.onStop()

        actor.send(AuthPresenterActor.InMsg.View.OnLoginViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()
    }


    companion object {

        private const val FB_EMAIL = "email"
        private const val FB_USER_POSTS = "user_posts"
        private const val FB_AUTH_TYPE = "rerequest"

        @JvmStatic
        fun newInstance(actor: AuthPresenterActor) =

            LoginFragment().apply {

                this.actor = actor

            }

    }

}

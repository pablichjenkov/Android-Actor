package com.hamperapp.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hamperapp.R
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
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

            actor.send(AuthPresenterActor.InMsg.View.DoSignUp)

        }

    }

    override fun onStart() {
        super.onStart()

        actor.fragmentChannel = fragmentCoroutineScope.actor {

            consumeEach { event: Any ->

                when (event) {

                    AuthPresenterActor.OutMsg.View.OnLoad -> {

                        progressBar.visibility = View.VISIBLE

                    }

                    AuthPresenterActor.OutMsg.View.OnSuccess -> {

                        progressBar.visibility = View.GONE

                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()

                    }

                    AuthPresenterActor.OutMsg.View.OnError -> {

                        progressBar.visibility = View.GONE

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

    companion object {

        @JvmStatic
        fun newInstance(actor: AuthPresenterActor) = SignupFragment().apply { this.actor = actor }

    }

}

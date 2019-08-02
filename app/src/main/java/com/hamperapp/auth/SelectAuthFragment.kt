package com.hamperapp.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hamperapp.R
import kotlinx.android.synthetic.main.fragment_select_auth.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class SelectAuthFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var actor: AuthPresenterActor


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectLoginBtn.setOnClickListener {

            actor.send(AuthPresenterActor.InMsg.View.ShowLogin)

        }

        selectSignupBtn.setOnClickListener {

            actor.send(AuthPresenterActor.InMsg.View.ShowSignUp)

        }

    }

    override fun onStart() {
        super.onStart()

        actor.fragmentChannel = fragmentCoroutineScope.actor {

            consumeEach { event: Any ->

                when (event) { }

            }

        }

        actor.send(AuthPresenterActor.InMsg.View.OnSelectAuthViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(AuthPresenterActor.InMsg.View.OnSelectAuthViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()
    }

    companion object {

        @JvmStatic
        fun newInstance(actor: AuthPresenterActor) =

            SelectAuthFragment().apply {

                this.actor = actor

            }

    }

}

package com.hamperapp.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hamperapp.R
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class SplashFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var actor: SplashActor


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onStart() {
        super.onStart()

        actor.fragmentChannel = fragmentCoroutineScope.actor {

            consumeEach { event ->

                when (event) {

                    SplashActor.OutMsg.View.OnLoad -> {

                        progressBar.visibility = View.VISIBLE

                    }

                    SplashActor.OutMsg.View.OnSuccess -> {

                        progressBar.visibility = View.GONE

                    }

                    SplashActor.OutMsg.View.OnError -> {

                        progressBar.visibility = View.GONE

                    }

                }

            }

        }

        actor.send(SplashActor.InMsg.View.OnViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(SplashActor.InMsg.View.OnViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()

    }

    companion object {

        @JvmStatic
        fun newInstance(actor: SplashActor) =

            SplashFragment().apply {

                this.actor = actor

            }

    }

}

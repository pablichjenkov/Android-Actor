package com.hamperapp.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hamperapp.R
import kotlinx.android.synthetic.main.fragment_support.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class SupportFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var mailboxChannel: SendChannel<SupportActor.OutMsg.View>

    private lateinit var actor: SupportActor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mailboxChannel = fragmentCoroutineScope.actor {

            consumeEach { event ->

                when (event) {

                    SupportActor.OutMsg.View.OnLoad -> {

                        progressBar.visibility = View.VISIBLE

                    }

                    SupportActor.OutMsg.View.OnSuccess -> {

                        progressBar.visibility = View.GONE

                    }

                    SupportActor.OutMsg.View.OnError -> {

                        progressBar.visibility = View.GONE

                    }

                }

            }

        }

        actor.fragmentSink = mailboxChannel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_support, container, false)
    }

    override fun onStart() {
        super.onStart()

        actor.send(SupportActor.InMsg.View.OnViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(SupportActor.InMsg.View.OnViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()

    }

    companion object {

        @JvmStatic
        fun newInstance(actor: SupportActor) =

            SupportFragment().apply {

                this.actor = actor

            }

    }

}

package com.hamperapp.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hamperapp.R
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class SettingsFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var mailboxChannel: SendChannel<SettingsActor.OutMsg.View>

    private lateinit var actor: SettingsActor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mailboxChannel = fragmentCoroutineScope.actor {

            consumeEach { event ->

                when (event) {

                    SettingsActor.OutMsg.View.OnLoad -> {

                        progressBar.visibility = View.VISIBLE

                    }

                    SettingsActor.OutMsg.View.OnSuccess -> {

                        progressBar.visibility = View.GONE

                    }

                    SettingsActor.OutMsg.View.OnError -> {

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

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onStart() {
        super.onStart()

        actor.send(SettingsActor.InMsg.View.OnViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(SettingsActor.InMsg.View.OnViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()

    }

    companion object {

        @JvmStatic
        fun newInstance(actor: SettingsActor) =

            SettingsFragment().apply {

                this.actor = actor

            }

    }

}

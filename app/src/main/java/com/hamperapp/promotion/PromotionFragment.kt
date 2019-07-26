package com.hamperapp.promotion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hamperapp.R
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class PromotionFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var mailboxChannel: SendChannel<PromotionActor.OutMsg.View>

    private lateinit var actor: PromotionActor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mailboxChannel = fragmentCoroutineScope.actor {

            consumeEach { event ->

                when (event) {

                    PromotionActor.OutMsg.View.OnLoad -> {

                        progressBar.visibility = View.VISIBLE

                    }

                    PromotionActor.OutMsg.View.OnSuccess -> {

                        progressBar.visibility = View.GONE

                    }

                    PromotionActor.OutMsg.View.OnError -> {

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

        return inflater.inflate(R.layout.fragment_promotion, container, false)
    }

    override fun onStart() {
        super.onStart()

        actor.send(PromotionActor.InMsg.View.OnViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(PromotionActor.InMsg.View.OnViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()

    }

    companion object {

        @JvmStatic
        fun newInstance(actor: PromotionActor) =

            PromotionFragment().apply {

                this.actor = actor

            }

    }

}

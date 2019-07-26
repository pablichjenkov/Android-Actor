package com.hamperapp.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hamperapp.R
import kotlinx.android.synthetic.main.fragment_order_history.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class OrderHistoryFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var mailboxChannel: SendChannel<OrderHistoryhActor.OutMsg.View>

    private lateinit var actor: OrderHistoryhActor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mailboxChannel = fragmentCoroutineScope.actor {

            consumeEach { event ->

                when (event) {

                    OrderHistoryhActor.OutMsg.View.OnLoad -> {

                        progressBar.visibility = View.VISIBLE

                    }

                    OrderHistoryhActor.OutMsg.View.OnSuccess -> {

                        progressBar.visibility = View.GONE

                    }

                    OrderHistoryhActor.OutMsg.View.OnError -> {

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

        return inflater.inflate(R.layout.fragment_order_history, container, false)
    }

    override fun onStart() {
        super.onStart()

        actor.send(OrderHistoryhActor.InMsg.View.OnViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(OrderHistoryhActor.InMsg.View.OnViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()

    }

    companion object {

        @JvmStatic
        fun newInstance(actor: OrderHistoryhActor) =

            OrderHistoryFragment().apply {

                this.actor = actor

            }

    }

}

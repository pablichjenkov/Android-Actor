package com.hamperapp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hamperapp.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class HomeFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var mailboxChannel: SendChannel<HomeActor.OutMsg.View>


    private lateinit var actor: HomeActor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mailboxChannel = fragmentCoroutineScope.actor {

            consumeEach { event ->

                when (event) {

                    HomeActor.OutMsg.View.OnLoad -> {


                    }

                    HomeActor.OutMsg.View.OnSuccess -> {


                    }

                    HomeActor.OutMsg.View.OnError -> {


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

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dryCleaningBtn.setOnClickListener {

            actor.send(HomeActor.InMsg.View.OnDryCleanClick)

        }

        washFoldBtn.setOnClickListener {

            actor.send(HomeActor.InMsg.View.OnWashAndFoldClick)

        }

        laundryBtn.setOnClickListener {

            actor.send(HomeActor.InMsg.View.OnLaundryClick)

        }

    }

    override fun onStart() {
        super.onStart()

        actor.send(HomeActor.InMsg.View.OnViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(HomeActor.InMsg.View.OnViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()

    }

    companion object {

        @JvmStatic
        fun newInstance(actor: HomeActor) =

            HomeFragment().apply {

                this.actor = actor

            }

    }

}

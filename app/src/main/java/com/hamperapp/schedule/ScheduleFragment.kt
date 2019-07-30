package com.hamperapp.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hamperapp.R
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class ScheduleFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var actor: ScheduleActor


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        next.setOnClickListener {

            actor.send(ScheduleActor.InMsg.View.OnNextClick)

        }

    }

    override fun onStart() {
        super.onStart()

        actor.fragmentChannel = fragmentCoroutineScope.actor {

            consumeEach { event ->

                when (event) {

                    ScheduleActor.OutMsg.View.OnLoad -> {

                        progressBar.visibility = View.VISIBLE

                    }

                    ScheduleActor.OutMsg.View.OnSuccess -> {

                        progressBar.visibility = View.GONE

                    }

                    ScheduleActor.OutMsg.View.OnError -> {

                        progressBar.visibility = View.GONE

                    }

                }

            }

        }

        actor.send(ScheduleActor.InMsg.View.OnViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(ScheduleActor.InMsg.View.OnViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()

    }

    companion object {

        @JvmStatic
        fun newInstance(actor: ScheduleActor) =

            ScheduleFragment().apply {

                this.actor = actor

            }

    }

}

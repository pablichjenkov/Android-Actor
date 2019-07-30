package com.hamperapp.schedule

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


class ScheduleActor(
    private var uiSendChannel: SendChannel<UIActorMsg>
) : Actor<ScheduleActor.InMsg>() {

	lateinit var parentChannel: SendChannel<OutMsg>

	lateinit var fragmentChannel: SendChannel<OutMsg.View>


	override fun start() {
		super.start()

		val titleMsg = UIActorMsg.SetTitle("Schedule Screen")

		val scheduleFragment = ScheduleFragment.newInstance(this)

		val uiMsg = UIActorMsg.SetFragment(scheduleFragment, "scheduleFragment")

		scope.launch {

			uiSendChannel.send(titleMsg)

			uiSendChannel.send(uiMsg)

		}

	}

    override fun onAction(inMsg: InMsg) {

        when (inMsg) {

            InMsg.View.OnViewReady -> {}

			InMsg.View.OnNextClick -> {

				scope.launch {

					parentChannel.send(OutMsg.OnScheduleComplete)

				}

			}

            InMsg.View.OnViewStop -> {}

        }

    }

	override fun back() {
		super.back()

		scope.launch {

			parentChannel.send(OutMsg.OnScheduleCancel)

		}

	}


    sealed class InMsg {

        sealed class View : InMsg() {

            object OnViewReady : View()

			object OnNextClick : View()

            object OnViewStop : View()

        }

    }

    sealed class OutMsg {

        object OnScheduleComplete : OutMsg()

		object OnScheduleCancel : OutMsg()

        sealed class View : OutMsg() {

            object OnLoad : View()

            object OnSuccess : View()

            object OnError : View()

        }

    }

}

package com.hamperapp.home

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeActor(
    private var uiSendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>?
) : Actor<HomeActor.InMsg>() {

	lateinit var fragmentSink: SendChannel<OutMsg.View>


	override fun start() {
		super.start()

		val titleMsg = UIActorMsg.SetTitle("Home Screen")

		val homeFragment = HomeFragment.newInstance(this)

		val uiMsg = UIActorMsg.SetFragment(homeFragment, "homeFragment")

		scope.launch {

			uiSendChannel.send(titleMsg)

			uiSendChannel.send(uiMsg)

		}

	}

    override fun onAction(inMsg: InMsg) {

        when (inMsg) {

            InMsg.View.OnViewReady -> {

                scope.launch {

                    fragmentSink.send(OutMsg.View.OnLoad)

                    delay(2000)

                    fragmentSink.send(OutMsg.View.OnSuccess)

                    delay(1000)

                    observerChannel?.send(OutMsg.OnHomeComplete)

                }

            }

            InMsg.View.OnViewStop -> {}

        }

    }

	override fun back() {
		super.back()

		observerChannel = null

		scope.cancel()

	}


    sealed class InMsg {

        sealed class View : InMsg() {

            object OnViewReady : View()

            object OnViewStop : View()

        }

    }

    sealed class OutMsg {

        object OnHomeComplete : OutMsg()


        sealed class View : OutMsg() {

            object OnLoad : View()

            object OnSuccess : View()

            object OnError : View()

        }

    }

}

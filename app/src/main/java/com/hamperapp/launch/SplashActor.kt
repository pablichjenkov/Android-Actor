package com.hamperapp.launch

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActor(
    private var uiSendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>?
) : Actor<SplashActor.InMsg>() {

	lateinit var fragmentSink: SendChannel<OutMsg.View>


	override fun start() {
		super.start()

		val titleMsg = UIActorMsg.SetTitle("Splash Screen")

		val splashFragment = SplashFragment.newInstance(this)

		val uiMsg = UIActorMsg.SetFragment(splashFragment, "splashFragment")

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

                    observerChannel?.send(OutMsg.OnSplashComplete)

                }

            }

            InMsg.View.OnViewStop -> {}

        }

    }

	override fun back() {
		super.back()

		scope.launch {
			uiSendChannel.send(UIActorMsg.BackResult(false))
		}

		observerChannel = null

	}


    sealed class InMsg {

        sealed class View : InMsg() {

            object OnViewReady : View()

            object OnViewStop : View()

        }

    }

    sealed class OutMsg {

        object OnSplashComplete : OutMsg()


        sealed class View : OutMsg() {

            object OnLoad : View()

            object OnSuccess : View()

            object OnError : View()

        }

    }

}

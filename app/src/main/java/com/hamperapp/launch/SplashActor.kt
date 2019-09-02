package com.hamperapp.launch

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActor(
    private var uiSendChannel: SendChannel<UIActorMsg>
) : Actor<SplashActor.InMsg>() {

	lateinit var parentChannel: SendChannel<OutMsg>

	lateinit var fragmentChannel: SendChannel<OutMsg.View>


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

					fragmentChannel.send(OutMsg.View.ShowZipInput)

                }

            }

            InMsg.View.OnViewStop -> {}

            is InMsg.View.OnZipcodeEnter -> {

                // TODO: Validate zipcode is covered by Hamper Service
                // TODO: Save zipcode in local storage
                inMsg.zipcode

                scope.launch {

                    fragmentChannel.send(OutMsg.View.OnLoad)

                    delay(2000)

					fragmentChannel.send(OutMsg.View.OnSuccess)

                    delay(500)

					parentChannel.send(OutMsg.OnSplashComplete)

                }

            }

        }

    }

	override fun back() {
		super.back()

		scope.launch {
			uiSendChannel.send(UIActorMsg.BackResult(false))
		}

	}


    sealed class InMsg {

        sealed class View : InMsg() {

            object OnViewReady : View()

            object OnViewStop : View()

            class OnZipcodeEnter(val zipcode: String) : View()

        }

    }

    sealed class OutMsg {

        object OnSplashComplete : OutMsg()


        sealed class View : OutMsg() {

            object ShowZipInput : View()

            object OnLoad : View()

            object OnSuccess : View()

            object OnError : View()

        }

    }

}

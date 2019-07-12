package com.hamperapp.launch

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.BaseActor
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel


class SplashActor(
    private var uiSendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>?
) : BaseActor<SplashActor.InMsg>() {

	lateinit var fragmentSink: SendChannel<OutMsg.View>


	override fun onCommonAction(commonMsg: BaseActor.InMsg) {

		when (commonMsg) {

			BaseActor.InMsg.OnStart -> {

				val titleMsg = UIActorMsg.SetTitle("Splash Screen")

				val splashFragment = SplashFragment.newInstance(this)

				val uiMsg = UIActorMsg.SetFragment(splashFragment, "splashFragment")

				scope.launch {

					uiSendChannel.send(titleMsg)

					uiSendChannel.send(uiMsg)

				}

			}

			BaseActor.InMsg.OnStop -> {}

			BaseActor.InMsg.OnBack -> {

				observerChannel = null

				scope.cancel()

			}


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

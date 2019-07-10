package com.hamperapp.launch

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel


class SplashActor(
    var uiSendChannel: SendChannel<UIActorMsg>,
    var observerChannel: SendChannel<OutMsg>? = null
) : Actor<SplashActor.InMsg>() {

    lateinit var fragmentSink: SendChannel<OutMsg.View>

    override fun onAction(inMsg: InMsg) {

        when (inMsg) {

            InMsg.OnStart -> {

                val titleMsg = UIActorMsg.SetTitle("Splash Screen")

                val splashFragment = SplashFragment.newInstance(this)

                val uiMsg = UIActorMsg.SetView(splashFragment, "splashFragment")

                scope.launch {

                    uiSendChannel.send(titleMsg)

                    uiSendChannel.send(uiMsg)

                }

            }

            InMsg.OnStop -> {

                scope.coroutineContext.cancelChildren()

            }

            InMsg.OnBack -> {

                observerChannel = null

                scope.cancel()

            }

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

        object OnStart : InMsg()

        object OnStop : InMsg()

        object OnBack : InMsg()


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

package com.hamperapp.launch

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import com.hamperapp.network.http.Http
import com.hamperapp.network.http.asFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onAction(inMsg: InMsg) {

        when (inMsg) {

            InMsg.View.OnViewReady -> {

                scope.launch {

					//fragmentChannel.send(OutMsg.View.OnLoad)

                    //delay(500)

                    //fragmentChannel.send(OutMsg.View.OnLoadFinish)

                    delay(500)

                    parentChannel.send(OutMsg.OnSplashComplete)

                }

            }

            InMsg.View.OnViewStop -> {}

            /*
            is InMsg.View.OnZipcodeEnter -> {

                // TODO: Validate zipcode is covered by Hamper Service
                // TODO: Save zipcode in local storage

                scope.launch {

                    fragmentChannel.send(OutMsg.View.OnLoad)

                    val commonApi = Http.provideCommonApi


                    commonApi
                        .checkZipCode(
                            "Bearer Empty"
                            , inMsg.zipcode
                        )
                        .asFlow()
                        .catch { throwable ->

                            fragmentChannel.send(
                                OutMsg.View.OnZipcodeError(throwable.message.orEmpty())
                            )

                        }
                        .collect { zipcodeResp ->

                            if (zipcodeResp.checked) {

                                fragmentChannel.send(OutMsg.View.OnZipcodeSuccess)

                                delay(500)

                                parentChannel.send(OutMsg.OnSplashComplete)

                            } else {

                                fragmentChannel.send(
                                    OutMsg.View.OnZipcodeError("This app is not available in your area")
                                )

                            }

                        }


                }

            }

            */
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

            //class OnZipcodeEnter(val zipcode: String) : View()

        }

    }

    sealed class OutMsg {

        object OnSplashComplete : OutMsg()


        sealed class View : OutMsg() {

            //object ShowZipInput : View()

            object OnLoad : View()

            object OnLoadFinish : View()

            //object OnZipcodeSuccess : View()

            //class OnZipcodeError(val message: String) : View()

        }

    }

}

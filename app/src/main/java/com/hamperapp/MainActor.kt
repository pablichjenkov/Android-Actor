package com.hamperapp

import com.hamperapp.actor.Actor
import com.hamperapp.auth.AuthPresenterActor
import com.hamperapp.launch.SplashActor
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class MainActor(sendChannel: SendChannel<UIActorMsg>) : Actor<MainActor.InMsg>(

) {

	enum class Stage {
		Idle,
		Splash,
		AuthLogin,
		AuthRegister,
		Home
	}

	private var stage: Stage = Stage.Idle

	var authActor = HamperApplication.instance.authActor

	var splashActor = SplashActor(sendChannel, createSplashActorObserverChannel())

	var authPresenterActor = AuthPresenterActor(authActor = authActor, uiSendChannel = sendChannel)

	private val compositeDisposable = CompositeDisposable()


	override fun onAction(inMsg: InMsg) {

		when (inMsg) {

			InMsg.OnStart -> {

				onStart()

			}

			InMsg.OnStop -> {

				onStop()

			}

			InMsg.OnBack -> {

				OnBack()

			}

		}

	}

	private fun createSplashActorObserverChannel() : SendChannel<SplashActor.OutMsg> = scope.actor(Dispatchers.Main) {

		consumeEach { splashMsg ->

			when (splashMsg) {

				is SplashActor.OutMsg.OnSplashComplete -> {

					authPresenterActor.send(AuthPresenterActor.InMsg.OnStart)

				}

			}

		}

	}

	private fun onStart() {

		when (stage) {

			Stage.Idle -> {

				stage = Stage.Splash

				splashActor.send(SplashActor.InMsg.OnStart)

			}

			Stage.Splash -> {


			}

			Stage.AuthLogin -> {


			}

			Stage.AuthRegister -> {


			}

			Stage.Home -> {

			}

		}

	}

	private fun onStop() {

		compositeDisposable.clear()

	}

	private fun OnBack() {

		stage = Stage.Idle

	}

	sealed class InMsg {

		object OnStart : InMsg()

		object OnStop : InMsg()

		object OnBack : InMsg()

	}


}

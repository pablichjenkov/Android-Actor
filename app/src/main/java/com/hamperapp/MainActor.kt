package com.hamperapp

import com.hamperapp.actor.Actor
import com.hamperapp.auth.AuthPresenterActor
import com.hamperapp.launch.SplashActor
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class MainActor(
	private val uiSendChannel: SendChannel<UIActorMsg>) : Actor<MainActor.InMsg>(
) {

	enum class Stage {
		Idle,
		Splash,
		Auth,
		Home
	}

	private var stage: Stage = Stage.Idle

	private var authActor = HamperApplication.instance.authActor

	private var splashActor = SplashActor(uiSendChannel, createSplashActorObserverChannel())

	private var authPresenterActor = AuthPresenterActor(
		authActor = authActor,
		uiSendChannel = uiSendChannel,
		observerChannel = createAuthPresenterActorObserverChannel()
	)

	private val compositeDisposable = CompositeDisposable()


	override fun onAction(inMsg: InMsg) {

		when (inMsg) {

			InMsg.OnStart -> { onStart() }

			InMsg.OnStop -> { onStop() }

			InMsg.OnBack -> { OnBack() }

		}

	}

	private fun createSplashActorObserverChannel() : SendChannel<SplashActor.OutMsg> = scope.actor {

		consumeEach { splashMsg ->

			when (splashMsg) {

				is SplashActor.OutMsg.OnSplashComplete -> {

					stage = Stage.Auth

					authPresenterActor.send(AuthPresenterActor.InMsg.OnStart)

				}

			}

		}

	}

	private fun createAuthPresenterActorObserverChannel() : SendChannel<AuthPresenterActor.OutMsg> = scope.actor {

		consumeEach { authPMsg ->

			when (authPMsg) {

				is AuthPresenterActor.OutMsg.AuthSuccess -> {


				}

				is AuthPresenterActor.OutMsg.AuthError -> {

					uiSendChannel.send(UIActorMsg.BackResult(false))

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
				// Let SplashFragment.onStart() propagate this event to SplashActor()
			}

			Stage.Auth -> {
				// Let AuthFragments.onStart() propagate this event to AuthPresenterActor()
			}

			Stage.Home -> {

			}

		}

	}

	private fun onStop() {

		compositeDisposable.clear()

	}

	private fun OnBack() {

		when (stage) {

			Stage.Idle -> {

				splashActor.close()

				authPresenterActor.close()

			}

			Stage.Splash -> {

				splashActor.send(SplashActor.InMsg.OnBack)

			}

			Stage.Auth -> {

				authPresenterActor.send(AuthPresenterActor.InMsg.OnBack)

			}

			Stage.Home -> {

			}

		}

	}

	sealed class InMsg {

		object OnStart : InMsg()

		object OnStop : InMsg()

		object OnBack : InMsg()

	}

}

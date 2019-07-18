package com.hamperapp

import com.hamperapp.actor.Actor
import com.hamperapp.auth.AuthPresenterActor
import com.hamperapp.launch.SplashActor
import com.hamperapp.navigation.NavigationActor
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class MainActor(
	private val uiSendChannel: SendChannel<UIActorMsg>
): Actor<MainActor.InMsg>() {

	enum class Stage {
		Idle,
		Splash,
		Auth,
		Drawer
	}

	private var stage: Stage = Stage.Idle

	private var activeActor: Actor<*>? = null

	private var authActor = HamperApplication.instance.authActor

	private var splashActor = SplashActor(uiSendChannel, createSplashActorObserverChannel())

	private var authPresenterActor = AuthPresenterActor(
		authActor = authActor,
		uiSendChannel = uiSendChannel,
		observerChannel = createAuthPresenterActorObserverChannel()
	)

	private var drawerUIActor = NavigationActor(uiSendChannel, createDrawerUIActorObserverChannel())

	private val compositeDisposable = CompositeDisposable()


	override fun start() {
		super.start()

		onStart()
	}

	override fun onAction(msg: InMsg) {}

	override fun stop() {
		super.stop()

		onStop()
	}

	override fun back() {
		super.back()

		onBack()
	}

	private fun createSplashActorObserverChannel() : SendChannel<SplashActor.OutMsg> = scope.actor {

		consumeEach { splashMsg ->

			when (splashMsg) {

				is SplashActor.OutMsg.OnSplashComplete -> {

					stage = Stage.Auth

					activeActor?.stop()

					authPresenterActor.start()

					activeActor = authPresenterActor

				}

			}

		}

	}

	private fun createAuthPresenterActorObserverChannel() : SendChannel<AuthPresenterActor.OutMsg> = scope.actor {

		consumeEach { authPMsg ->

			when (authPMsg) {

				is AuthPresenterActor.OutMsg.AuthSuccess -> {

					stage = Stage.Drawer

					activeActor?.stop()

					drawerUIActor.start()

					activeActor = drawerUIActor

				}

				is AuthPresenterActor.OutMsg.AuthError -> {

					if (authPMsg.error.contains("Back")) {
						uiSendChannel.send(UIActorMsg.BackResult(false))
					}

				}

			}

		}

	}

	private fun createDrawerUIActorObserverChannel() : SendChannel<NavigationActor.OutMsg> = scope.actor {

		consumeEach { authPMsg ->

			when (authPMsg) {

				is NavigationActor.OutMsg.OnDrawerComplete -> {
					uiSendChannel.send(UIActorMsg.BackResult(false))
				}

			}

		}

	}

	private fun onStart() {

		when (stage) {

			Stage.Idle -> {

				stage = Stage.Splash

				splashActor.start()

				activeActor = splashActor

			}

			Stage.Splash -> {
				// Let SplashFragment.onStart() propagate this event to SplashActor()
			}

			Stage.Auth -> {
				// Let AuthFragments.onStart() propagate this event to AuthPresenterActor()
			}

			Stage.Drawer -> {
				// Let DrawerFragment.onStart() propagate this event to NavigationActor()
			}

		}

	}

	private fun onStop() {

		compositeDisposable.clear()

	}

	private fun onBack() {

		when (stage) {

			Stage.Idle -> {

				splashActor.close()

				authPresenterActor.close()

				drawerUIActor.close()

			}

			else -> {

				activeActor?.back()

			}

		}

	}


	sealed class InMsg

}

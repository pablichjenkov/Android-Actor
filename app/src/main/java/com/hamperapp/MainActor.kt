package com.hamperapp

import com.hamperapp.actor.Actor
import com.hamperapp.auth.AuthPresenterActor
import com.hamperapp.launch.SplashActor
import com.hamperapp.navigation.NavigationActor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch


class MainActor(
	private val uiSendChannel: SendChannel<UIActorMsg>
): Actor<MainActor.InMsg>() {

	private var activeActor: Actor<*>? = null

	private var authActor = HamperApplication.instance.authActor

	private var splashActor = SplashActor(uiSendChannel)

	private var authPresenterActor = AuthPresenterActor(authActor, uiSendChannel)

	private var drawerUIActor = NavigationActor(uiSendChannel)


	override fun start() {
		super.start()

		// Update fresh listener channels in every children Actor

		splashActor.parentChannel = createSplashActorObserverChannel()

		authPresenterActor.parentChannel = createAuthPresenterActorObserverChannel()

		drawerUIActor.parentChannel = createDrawerUIActorObserverChannel()

		if (activeActor == null) {

			setActiveActor(splashActor)

		}

	}

	override fun onAction(inMsg: InMsg) {}

	override fun back() {
		super.back()

		when (activeActor) {

			null -> {

				splashActor.close()

				authPresenterActor.close()

				drawerUIActor.close()

				close()
			}

			else -> {

				activeActor?.back()

			}

		}

	}

	private fun createSplashActorObserverChannel() : SendChannel<SplashActor.OutMsg> = scope.actor {

		consumeEach { splashMsg ->

			when (splashMsg) {

				is SplashActor.OutMsg.OnSplashComplete -> {

					setActiveActor(authPresenterActor)

				}

			}

		}

	}

	private fun createAuthPresenterActorObserverChannel() : SendChannel<AuthPresenterActor.OutMsg> = scope.actor {

		consumeEach { authPMsg ->

			when (authPMsg) {

				is AuthPresenterActor.OutMsg.AuthSuccess -> {

					setActiveActor(drawerUIActor)

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

	private fun setActiveActor(nextActor: Actor<*>?) {

		scope.launch {

			activeActor?.stop()

			nextActor?.start()

			activeActor = nextActor

		}

	}


	sealed class InMsg

}

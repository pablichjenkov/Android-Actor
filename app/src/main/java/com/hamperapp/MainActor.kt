package com.hamperapp

import com.hamperapp.actor.BaseActor
import com.hamperapp.auth.AuthPresenterActor
import com.hamperapp.launch.SplashActor
import com.hamperapp.navigation.DrawerUIActor
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class MainActor(
	private val uiSendChannel: SendChannel<UIActorMsg>) : BaseActor<MainActor.InMsg>(
) {

	enum class Stage {
		Idle,
		Splash,
		Auth,
		Drawer
	}

	private var stage: Stage = Stage.Idle

	private var activeActor: BaseActor<*>? = null

	private var authActor = HamperApplication.instance.authActor

	private var splashActor = SplashActor(uiSendChannel, createSplashActorObserverChannel())

	private var authPresenterActor = AuthPresenterActor(
		authActor = authActor,
		uiSendChannel = uiSendChannel,
		observerChannel = createAuthPresenterActorObserverChannel()
	)

	private var drawerUIActor = DrawerUIActor(uiSendChannel, createDrawerUIActorObserverChannel())

	private val compositeDisposable = CompositeDisposable()


	override fun onCommonAction(commonMsg: BaseActor.InMsg) {

		when (commonMsg) {

			BaseActor.InMsg.OnStart -> { onStart() }

			BaseActor.InMsg.OnStop -> { onStop() }

			BaseActor.InMsg.OnBack -> { OnBack() }

		}

	}

	override fun onAction(inMsg: InMsg) {}

	private fun createSplashActorObserverChannel() : SendChannel<SplashActor.OutMsg> = scope.actor {

		consumeEach { splashMsg ->

			when (splashMsg) {

				is SplashActor.OutMsg.OnSplashComplete -> {

					stage = Stage.Auth

					activeActor?.sendCommonMsg(BaseActor.InMsg.OnStop)

					authPresenterActor.sendCommonMsg(BaseActor.InMsg.OnStart)

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

					activeActor?.sendCommonMsg(BaseActor.InMsg.OnStop)

					drawerUIActor.sendCommonMsg(BaseActor.InMsg.OnStart)

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

	private fun createDrawerUIActorObserverChannel() : SendChannel<DrawerUIActor.OutMsg> = scope.actor {

		consumeEach { authPMsg ->

			when (authPMsg) {

				is DrawerUIActor.OutMsg.OnDrawerComplete -> {
					uiSendChannel.send(UIActorMsg.BackResult(false))
				}

			}

		}

	}

	private fun onStart() {

		when (stage) {

			Stage.Idle -> {

				stage = Stage.Splash

				splashActor.sendCommonMsg(BaseActor.InMsg.OnStart)

				activeActor = splashActor

			}

			Stage.Splash -> {
				// Let SplashFragment.onStart() propagate this event to SplashActor()
			}

			Stage.Auth -> {
				// Let AuthFragments.onStart() propagate this event to AuthPresenterActor()
			}

			Stage.Drawer -> {
				// Let DrawerFragment.onStart() propagate this event to DrawerUIActor()
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

				drawerUIActor.close()

			}

			else -> {

				activeActor?.sendCommonMsg(BaseActor.InMsg.OnBack)

			}

		}

	}

	sealed class InMsg {}

}

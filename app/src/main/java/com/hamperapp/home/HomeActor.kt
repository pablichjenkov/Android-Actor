package com.hamperapp.home

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import com.hamperapp.order.OrderWizardActor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch


class HomeActor(
    private var uiSendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>?
) : Actor<HomeActor.InMsg>() {

	lateinit var fragmentSink: SendChannel<OutMsg.View>

	private var dryCleaningOrderWizard = OrderWizardActor(uiSendChannel)

	private var washFoldOrderWizard = OrderWizardActor(uiSendChannel)

	private var laundryOrderWizard = OrderWizardActor(uiSendChannel)

	private var activeActor: Actor<*>? = null


	override fun start() {
		super.start()

		dryCleaningOrderWizard.parentChannel = createLaundryOrderListener()

		washFoldOrderWizard.parentChannel = createLaundryOrderListener()

		laundryOrderWizard.parentChannel = createLaundryOrderListener()

		showHomeItems()

	}

	override fun onAction(inMsg: InMsg) {

        when (inMsg) {

            InMsg.View.OnViewReady -> {

            }

			InMsg.View.OnDryCleanClick -> {

				setActiveActor(dryCleaningOrderWizard)

			}

			InMsg.View.OnWashAndFoldClick -> {

				setActiveActor(washFoldOrderWizard)

			}

			InMsg.View.OnLaundryClick -> {

				setActiveActor(laundryOrderWizard)

			}

            InMsg.View.OnViewStop -> {}

        }

    }

	override fun back() {

		activeActor?.let { activeActor ->

			activeActor.back()

		} ?: run {

			scope.launch {
				uiSendChannel.send(UIActorMsg.BackResult(false))
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

	private fun showHomeItems() {

		setActiveActor(null)

		val titleMsg = UIActorMsg.SetTitle("Home Screen")

		val homeFragment = HomeFragment.newInstance(this)

		val uiMsg = UIActorMsg.SetFragment(homeFragment, "homeFragment")

		scope.launch {

			uiSendChannel.send(titleMsg)

			uiSendChannel.send(uiMsg)

		}

	}

	private fun createLaundryOrderListener() = scope.actor<OrderWizardActor.OutMsg> {

		consumeEach { msg ->

			when (msg) {

				OrderWizardActor.OutMsg.OnOrderComplete -> {

					showHomeItems()

				}

				OrderWizardActor.OutMsg.OnOrderCancelled -> {

					showHomeItems()

				}

			}

		}

	}

    sealed class InMsg {

        sealed class View : InMsg() {

            object OnViewReady : View()

            object OnViewStop : View()

			object OnLaundryClick : View()

			object OnDryCleanClick : View()

			object OnWashAndFoldClick : View()

        }

    }

    sealed class OutMsg {

        sealed class View : OutMsg() {

            object OnLoad : View()

            object OnSuccess : View()

            object OnError : View()

        }

    }

}

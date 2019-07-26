package com.hamperapp.order

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import com.hamperapp.payment.PaymentActor
import com.hamperapp.schedule.ScheduleActor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.util.*


class OrderWizardActor(
    private var uiSendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>?
) : Actor<OrderWizardActor.InMsg>() {

	private lateinit var itemSelectorActor: ItemSelectorActor

	private lateinit var cartActor: CartActor

	private lateinit var scheduleActor: ScheduleActor

	private lateinit var paymentActor: PaymentActor

	private var activeActor: Actor<*>? = null

	private val stack: Stack<Actor<*>> = Stack()


	override fun start() {
		super.start()

		itemSelectorActor = ItemSelectorActor(uiSendChannel, createItemSelectorListener())

		cartActor = CartActor(uiSendChannel, createCartListener())

		scheduleActor = ScheduleActor(uiSendChannel, createScheduleListener())

		paymentActor = PaymentActor(uiSendChannel, createPaymentListener())

		pushActor(itemSelectorActor)

	}

    override fun onAction(inMsg: InMsg) {

        when (inMsg) {}

    }

	override fun back() {

		activeActor?.let {

			it.back()

		} ?: run {

			scope.launch {

				observerChannel?.send(OutMsg.OnOrderCancelled)

			}
		}

	}

	private fun pushActor(nextActor: Actor<*>) {

		activeActor?.let { currentActor ->

			stack.push(currentActor)

		}

		setActiveActor(nextActor)

	}

	private fun popActor() {

		val previousActor = stack.pop()

		if (previousActor != null) {

			setActiveActor(previousActor)

		} else {

			scope.launch {

				observerChannel?.send(OutMsg.OnOrderCancelled)

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

	private fun createItemSelectorListener() = scope.actor<ItemSelectorActor.OutMsg> {

		consumeEach { msg ->

			when (msg) {

				is ItemSelectorActor.OutMsg.OnItemSelectionComplete -> {

					if (msg.result.skipSelection) {

						pushActor(scheduleActor)

					} else {

						pushActor(cartActor)

					}

				}

				ItemSelectorActor.OutMsg.OnItemSelectionCancel -> {

					observerChannel?.send(OutMsg.OnOrderCancelled)

				}

			}

		}

	}

	private fun createCartListener() = scope.actor<CartActor.OutMsg> {

		consumeEach { msg ->

			when (msg) {

				is CartActor.OutMsg.OnCartComplete -> {

					pushActor(scheduleActor)

				}

				CartActor.OutMsg.OnCartCancel -> {

					popActor()

				}

			}

		}

	}

	private fun createScheduleListener() = scope.actor<ScheduleActor.OutMsg> {

		consumeEach { msg ->

			when (msg) {

				is ScheduleActor.OutMsg.OnScheduleComplete -> {

					pushActor(paymentActor)

				}

				ScheduleActor.OutMsg.OnScheduleCancel -> {

					popActor()

				}

			}

		}

	}

	private fun createPaymentListener() = scope.actor<PaymentActor.OutMsg> {

		consumeEach { msg ->

			when (msg) {

				is PaymentActor.OutMsg.OnPaymentComplete -> {

					val toastMsg = UIActorMsg.Toast("Thank You for Purchase")

					uiSendChannel.send(toastMsg)

				}

				PaymentActor.OutMsg.OnPaymentCancel -> {

					popActor()

				}

			}

		}

	}


    sealed class InMsg {}

    sealed class OutMsg {

        object OnOrderComplete : OutMsg()

		object OnOrderCancelled : OutMsg()

    }

}

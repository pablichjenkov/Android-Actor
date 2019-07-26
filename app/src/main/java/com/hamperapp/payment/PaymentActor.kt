package com.hamperapp.payment

import com.hamperapp.UIActorMsg
import com.hamperapp.collection.CollectionActor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


class PaymentActor(
    uiSendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>
) : CollectionActor<CollectionActor.InMsg>(uiSendChannel) {

	override var title = "Laundry Items Selection"


    override fun onAction(inMsg: CollectionActor.InMsg) {
		super.onAction(inMsg)

        when (inMsg) {

			InMsg.Test -> {}

			CollectionActor.InMsg.View.OnBottomViewClick -> {

				scope.launch {

					observerChannel.send(
						OutMsg.OnPaymentComplete(
							PaymentResult()
						)
					)

				}

			}

        }

    }

	override fun back() {

		scope.launch {

			observerChannel.send(OutMsg.OnPaymentCancel)

		}

	}


    sealed class InMsg : CollectionActor.InMsg() {

		object Test : InMsg()

    }

    sealed class OutMsg : CollectionActor.OutMsg() {

        class OnPaymentComplete(val result: PaymentResult) : OutMsg()

		object OnPaymentCancel : OutMsg()

    }

}

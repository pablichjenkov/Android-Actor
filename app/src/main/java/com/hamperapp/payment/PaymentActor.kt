package com.hamperapp.payment

import com.hamperapp.UIActorMsg
import com.hamperapp.collection.CollectionActor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


class PaymentActor(
    uiSendChannel: SendChannel<UIActorMsg>
) : CollectionActor<CollectionActor.InMsg>(uiSendChannel) {

	override var title = "Payment Screen"

	lateinit var parentChannel: SendChannel<OutMsg>


    override fun onAction(inMsg: CollectionActor.InMsg) {
		super.onAction(inMsg)

        when (inMsg) {

			InMsg.Test -> {}


        }

    }

	override fun back() {

		scope.launch {

			parentChannel.send(OutMsg.OnPaymentCancel)

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

package com.hamperapp.order

import com.hamperapp.UIActorMsg
import com.hamperapp.collection.CollectionActor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


class CartActor(
    uiSendChannel: SendChannel<UIActorMsg>
) : CollectionActor<CollectionActor.InMsg>(uiSendChannel) {

	override var title = "Cart"

	lateinit var parentChannel: SendChannel<OutMsg>


    override fun onAction(inMsg: CollectionActor.InMsg) {
		super.onAction(inMsg)

        when (inMsg) {

			InMsg.Test -> {}


        }

    }

	override fun back() {

		scope.launch {

			parentChannel.send(OutMsg.OnCartCancel)

		}

	}


    sealed class InMsg : CollectionActor.InMsg() {

		object Test : InMsg()

    }

    sealed class OutMsg : CollectionActor.OutMsg() {

        class OnCartComplete(val result: CartResult) : OutMsg()

		object OnCartCancel : OutMsg()

    }

}

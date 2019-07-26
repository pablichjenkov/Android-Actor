package com.hamperapp.order

import com.hamperapp.UIActorMsg
import com.hamperapp.collection.CollectionActor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


class CartActor(
    uiSendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>
) : CollectionActor<CollectionActor.InMsg>(uiSendChannel) {

	override var title = "Cart"


    override fun onAction(inMsg: CollectionActor.InMsg) {
		super.onAction(inMsg)

        when (inMsg) {

			InMsg.Test -> {}

			CollectionActor.InMsg.View.OnBottomViewClick -> {

				scope.launch {

					observerChannel.send(
						OutMsg.OnCartComplete(
							CartResult(true)
						)
					)

				}

			}

        }

    }

	override fun back() {

		scope.launch {

			observerChannel.send(OutMsg.OnCartCancel)

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

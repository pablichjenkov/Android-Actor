package com.hamperapp.laundry

import com.hamperapp.UIActorMsg
import com.hamperapp.collection.CollectionActor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


class ItemSelectorActor(
    uiSendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>
) : CollectionActor<CollectionActor.InMsg>(uiSendChannel) {

	override var title = "Laundry Items Selection"


    override fun onAction(inMsg: CollectionActor.InMsg) {
		super.onAction(inMsg)

        when (inMsg) {

			InMsg.Test -> {}

        }

    }

	override fun back() {

		scope.launch {

			observerChannel?.send(OutMsg.OnItemSelectionCancel)

		}

	}


    sealed class InMsg : CollectionActor.InMsg() {

		object Test : InMsg()

    }

    sealed class OutMsg : CollectionActor.OutMsg() {

        object OnItemSelectionComplete : OutMsg()

		object OnItemSelectionCancel : OutMsg()

    }

}

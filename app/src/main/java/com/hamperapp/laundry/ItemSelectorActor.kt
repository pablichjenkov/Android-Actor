package com.hamperapp.laundry

import com.hamperapp.UIActorMsg
import com.hamperapp.collection.CollectionActor
import kotlinx.coroutines.channels.SendChannel


class ItemSelectorActor(
    uiSendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>?
) : CollectionActor<CollectionActor.InMsg>(uiSendChannel) {

	override var title = "Laundry Items Selection"


    override fun onAction(inMsg: CollectionActor.InMsg) {
		super.onAction(inMsg)

        when (inMsg) {

			InMsg.Test -> {}

        }

    }

	override fun back() {
		super.back()

		observerChannel = null

		close()

	}


    sealed class InMsg : CollectionActor.InMsg() {

		object Test : InMsg()

    }

    sealed class OutMsg : CollectionActor.OutMsg() {

        object OnItemSelectionComplete : OutMsg()

    }

}

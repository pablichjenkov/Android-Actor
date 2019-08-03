package com.hamperapp.order

import com.hamperapp.UIActorMsg
import com.hamperapp.collection.CollectionActor
import com.hamperapp.collection.ParallaxCollectionActor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


class ItemSelectorActor(
    uiSendChannel: SendChannel<UIActorMsg>
) : ParallaxCollectionActor<ParallaxCollectionActor.InMsg>(uiSendChannel) {

	override var title = "Laundry Items Selection"

	lateinit var parentChannel: SendChannel<OutMsg>


    override fun onAction(inMsg: ParallaxCollectionActor.InMsg) {
		super.onAction(inMsg)

        when (inMsg) {

			ParallaxCollectionActor.InMsg.View.OnBottomViewClick -> {

				scope.launch {

					parentChannel.send(
						OutMsg.OnItemSelectionComplete(
							ItemSelectionResult(null, true)
						)
					)

				}

			}

        }

    }

	override fun back() {

		scope.launch {

			parentChannel.send(OutMsg.OnItemSelectionCancel)

		}

	}


    sealed class InMsg : CollectionActor.InMsg() {

		object Test : InMsg()

    }

    sealed class OutMsg : CollectionActor.OutMsg() {

        class OnItemSelectionComplete(val result: ItemSelectionResult) : OutMsg()

		object OnItemSelectionCancel : OutMsg()

    }

}

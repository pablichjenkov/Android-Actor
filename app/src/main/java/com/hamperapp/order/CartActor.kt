package com.hamperapp.order

import com.hamperapp.UIActorMsg
import com.hamperapp.collection.CollectionActor
import com.hamperapp.collection.ProductInfoCell
import com.hamperapp.collection.SimpleCell1
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItemList
import com.mikepenz.fastadapter.utils.DefaultItemListImpl
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


class CartActor(
    uiSendChannel: SendChannel<UIActorMsg>
) : CollectionActor<CollectionActor.InMsg>(uiSendChannel) {

	override var title = "Cart"

	lateinit var parentChannel: SendChannel<OutMsg>


    override fun onAction(inMsg: CollectionActor.InMsg) {

        when (inMsg) {

			CollectionActor.InMsg.View.OnViewReady -> {}

			CollectionActor.InMsg.View.OnViewStop -> {}

			InMsg.GetCartData -> {}

        }

    }

	override fun back() {

		scope.launch {

			parentChannel.send(OutMsg.OnCartCancel)

		}

	}

	private fun generateProducts(): IItemList<GenericItem> {

		val itemList = DefaultItemListImpl<GenericItem>()

		for (i in 1..20) {

			val simpleItem = ProductInfoCell().withName("Test $i")

			simpleItem.identifier = (100 + i).toLong()

			itemList.items.add(simpleItem)

			itemList.items.add(SimpleCell1())
		}

		return itemList
	}


    sealed class InMsg : CollectionActor.InMsg() {

		object GetCartData : InMsg()

    }

    sealed class OutMsg : CollectionActor.OutMsg() {

        class OnCartComplete(val result: CartResult) : OutMsg()

		object OnCartCancel : OutMsg()

    }

}

package com.hamperapp.payment

import com.hamperapp.UIActorMsg
import com.hamperapp.collection.CollectionActor
import com.hamperapp.collection.ProductInfoCell
import com.hamperapp.collection.SimpleCell1
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItemList
import com.mikepenz.fastadapter.utils.DefaultItemListImpl
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


class PaymentActor(
    uiSendChannel: SendChannel<UIActorMsg>
) : CollectionActor<CollectionActor.InMsg>(uiSendChannel) {

	override var title = "Payment Screen"

	lateinit var parentChannel: SendChannel<OutMsg>


	override fun onAction(inMsg: CollectionActor.InMsg) {

		when (inMsg) {

			CollectionActor.InMsg.View.OnViewReady -> {

				scope.launch {

					val productList = generateProducts()

					fragmentChannel.send(CollectionActor.OutMsg.View.OnUpdate(productList))

				}

			}

			CollectionActor.InMsg.View.OnViewStop -> {}

			InMsg.DoPayment -> {}

		}

	}


	override fun back() {

		scope.launch {

			parentChannel.send(OutMsg.OnPaymentCancel)

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

		object DoPayment : InMsg()

    }

    sealed class OutMsg : CollectionActor.OutMsg() {

        class OnPaymentComplete(val result: PaymentResult) : OutMsg()

		object OnPaymentCancel : OutMsg()

    }

}

package com.hamperapp.order

import com.hamperapp.UIActorMsg
import com.hamperapp.collection.ParallaxCollectionActor
import com.hamperapp.collection.ProductInfoCell
import com.hamperapp.collection.SimpleCell1
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItemList
import com.mikepenz.fastadapter.utils.DefaultItemListImpl
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


class ItemSelectorActor(
    uiSendChannel: SendChannel<UIActorMsg>
) : ParallaxCollectionActor<ParallaxCollectionActor.InMsg>(uiSendChannel) {

	override var title = "Laundry Items Selection"

	lateinit var parentChannel: SendChannel<OutMsg>


    override fun onAction(inMsg: ParallaxCollectionActor.InMsg) {

        when (inMsg) {

			ParallaxCollectionActor.InMsg.View.OnViewReady -> {

				scope.launch {

					val productList = generateProducts()

					fragmentChannel.send(ParallaxCollectionActor.OutMsg.View.OnUpdate(productList))

				}

			}

			ParallaxCollectionActor.InMsg.View.OnBottomViewClick -> {

				scope.launch {

					parentChannel.send(
						OutMsg.OnItemSelectionComplete(
							ItemSelectionResult(null, true)
						)
					)

				}

			}

			ParallaxCollectionActor.InMsg.View.OnViewStop -> {}

        }

    }

	override fun back() {

		scope.launch {

			parentChannel.send(OutMsg.OnItemSelectionCancel)

		}

	}

	private fun generateProducts() : IItemList<GenericItem> {

		val itemList = DefaultItemListImpl<GenericItem>()

		for (i in 1..20) {

			val simpleItem = ProductInfoCell().withName("Test $i")

			simpleItem.identifier = (100 + i).toLong()

			itemList.items.add(simpleItem)

			itemList.items.add(SimpleCell1())
		}

		return itemList
	}


    sealed class InMsg : ParallaxCollectionActor.InMsg() {

		object FetchProducts : InMsg()

    }

    sealed class OutMsg : ParallaxCollectionActor.OutMsg() {

        class OnItemSelectionComplete(val result: ItemSelectionResult) : OutMsg()

		object OnItemSelectionCancel : OutMsg()

    }

}

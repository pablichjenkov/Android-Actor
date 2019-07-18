package com.hamperapp.home

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import com.hamperapp.laundry.ItemSelectorActor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch


class HomeActor(
    private var uiSendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>?
) : Actor<HomeActor.InMsg>() {


	lateinit var fragmentSink: SendChannel<OutMsg.View>

	private var activeActor: Actor<*>? = null

	private lateinit var itemSelectorActor: ItemSelectorActor


	override fun start() {
		super.start()

		itemSelectorActor = ItemSelectorActor(uiSendChannel, createItemSelectorListener())

		activeActor = itemSelectorActor

		val titleMsg = UIActorMsg.SetTitle("Home Screen")

		val homeFragment = HomeFragment.newInstance(this)

		val uiMsg = UIActorMsg.SetFragment(homeFragment, "homeFragment")

		scope.launch {

			uiSendChannel.send(titleMsg)

			uiSendChannel.send(uiMsg)

		}

	}

    override fun onAction(inMsg: InMsg) {

        when (inMsg) {

            InMsg.View.OnViewReady -> {

            }

			InMsg.View.OnLaundryClick -> {

				itemSelectorActor.start()

			}

            InMsg.View.OnViewStop -> {}

        }

    }

	override fun back() {
		super.back()

		scope.launch {
			uiSendChannel.send(UIActorMsg.BackResult(false))
		}

		observerChannel = null

	}


	private fun createItemSelectorListener() = scope.actor<ItemSelectorActor.OutMsg> {

		consumeEach { msg ->

			when (msg) {

			}

		}

	}

    sealed class InMsg {

        sealed class View : InMsg() {

            object OnViewReady : View()

            object OnViewStop : View()

			object OnLaundryClick : View()

        }

    }

    sealed class OutMsg {

        object OnHomeComplete : OutMsg()


        sealed class View : OutMsg() {

            object OnLoad : View()

            object OnSuccess : View()

            object OnError : View()

        }

    }

}

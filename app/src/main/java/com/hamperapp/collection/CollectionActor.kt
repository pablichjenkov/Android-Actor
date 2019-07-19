package com.hamperapp.collection

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


abstract class CollectionActor<in T : CollectionActor.InMsg>(
    protected var uiSendChannel: SendChannel<UIActorMsg>
) : Actor<T>() {

	lateinit var fragmentSink: SendChannel<OutMsg.View>

	protected open var title: String = "Collection Fragment"

	override fun start() {
		super.start()

		scope.launch {

			val collectionFragment = CollectionFragment.newInstance(this@CollectionActor as CollectionActor<InMsg>)

			val uiMsg = UIActorMsg.SetFragment(collectionFragment, "collectionFragment")

			uiSendChannel.send(uiMsg)


			val titleMsg = UIActorMsg.SetTitle(title)

			uiSendChannel.send(titleMsg)

		}

	}

    override fun onAction(inMsg: T) {

        when (inMsg) {

            InMsg.View.OnViewReady -> {

                scope.launch {

					fragmentSink.send(OutMsg.View.OnSuccess)

                }

            }

            InMsg.View.OnViewStop -> {}

        }

    }


	open class InMsg {

        open class View : InMsg() {

            object OnViewReady : View()

            object OnViewStop : View()

        }

    }

    open class OutMsg {

		open class View : OutMsg() {

            object OnLoad : View()

            object OnSuccess : View()

            object OnError : View()

        }

    }

}

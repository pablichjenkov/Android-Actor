package com.hamperapp.collection

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


abstract class ParallaxCollectionActor<in T : ParallaxCollectionActor.InMsg> (
	private var  uiSendChannel: SendChannel<UIActorMsg>
): Actor<T> () {

	lateinit var fragmentChannel: SendChannel<OutMsg.View>

	protected open var title: String = "Parallax Collection Fragment"


	override fun start() {
		super.start()

		scope.launch {

			val parallaxCollectionFragment
				= ParallaxCollectionFragment
				.newInstance(this@ParallaxCollectionActor as ParallaxCollectionActor<InMsg>)

			val uiMsg = UIActorMsg.SetFragment(parallaxCollectionFragment, "parallaxCollectionFragment")

			uiSendChannel.send(uiMsg)

			val titleMsg = UIActorMsg.SetTitle(title)

			uiSendChannel.send(titleMsg)

		}

	}

    override fun onAction(inMsg: T) {

        when (inMsg) {

            InMsg.View.OnViewReady -> {

                scope.launch {

					fragmentChannel.send(OutMsg.View.OnSuccess)

                }

            }

			InMsg.View.OnViewStop -> {}

        }

    }


	open class InMsg : CollectionActor.InMsg() {

        open class View : InMsg() {

            object OnViewReady : View()

			object OnBottomViewClick : View()

            object OnViewStop : View()

        }

    }

    open class OutMsg : CollectionActor.OutMsg() {

		open class View : OutMsg() {

            object OnLoad : View()

            object OnSuccess : View()

            object OnError : View()

        }

    }

}

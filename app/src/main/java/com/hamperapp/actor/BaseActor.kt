package com.hamperapp.actor

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


abstract class BaseActor<T> : Actor<T>() {

	private lateinit var commonInputChannel: SendChannel<InMsg>

	private var wasStop: AtomicBoolean = AtomicBoolean(false)


	init {

		startCommonCoroutineActor()

	}

	fun sendCommonMsg(commonMsg: InMsg) {

		if (wasStop.compareAndSet(true, false)) {

			startPrincipalCoroutineActor()

			startCommonCoroutineActor()

		}

		scope.launch {
			commonInputChannel.send(commonMsg)
		}

	}

	override fun onClose() {
		commonInputChannel.close()
	}

	protected abstract fun onCommonAction(commonMsg: InMsg)

	private fun startCommonCoroutineActor() {

		commonInputChannel = scope.actor {

			consumeEach { commonMsg ->

				when (commonMsg) {

					InMsg.OnStop -> intersectOnStop()

				}

				onCommonAction(commonMsg)

			}

		}

	}

	/**
	 * Common method to not repeat the cancelChildren() call in every subclass.
	 * It will cancel both child Actor-Coroutines the Principal and the Common.
	 * */
	private fun intersectOnStop() {

		scope.coroutineContext.cancelChildren()

		wasStop.set(true)

	}


	sealed class InMsg {

		object OnStart : InMsg()

		object OnStop : InMsg()

		object OnBack : InMsg()

	}

	sealed class OutMsg {}

}
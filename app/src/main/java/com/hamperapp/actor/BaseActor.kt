package com.hamperapp.actor

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch


abstract class BaseActor<T> : Actor<T>() {

	private val commonInputChannel: SendChannel<InMsg>

	init {

		commonInputChannel = scope.actor {

			consumeEach { commonMsg ->

				onCommonAction(commonMsg)

			}

		}

	}

	fun sendCommonMsg(commonMsg: InMsg) {

		scope.launch {
			commonInputChannel.send(commonMsg)
		}

	}

	abstract fun onCommonAction(commonMsg: InMsg)

	sealed class InMsg {

		object OnStart : InMsg()

		object OnStop : InMsg()

		object OnBack : InMsg()

	}

	sealed class OutMsg {}

}
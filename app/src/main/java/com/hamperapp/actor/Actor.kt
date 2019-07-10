package com.hamperapp.actor

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


abstract class Actor<in T> {

	private val job = SupervisorJob()

	protected val scope = CoroutineScope(Dispatchers.IO + job)

	private val sendChannel: SendChannel<T>

	init {

		sendChannel = scope.actor {

			consumeEach {

				onAction(it)

			}

		}

	}

	fun send(inMsg: T) {

		scope.launch {

			sendChannel.send(inMsg)

		}

	}

	fun close() {
		sendChannel.close()
		scope.cancel()
	}

	protected abstract fun onAction(inMsg: T)

}

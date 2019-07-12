package com.hamperapp.actor

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


abstract class Actor<in T> {

	private val job = SupervisorJob()

	protected var scope = CoroutineScope(Dispatchers.IO + job)

	private val mainInputChannel: SendChannel<T>

	init {

		mainInputChannel = scope.actor {

			consumeEach {

				onAction(it)

			}

		}

	}

	fun send(inMsg: T) {

		scope.launch {

			mainInputChannel.send(inMsg)

		}

	}

	fun close() {
		mainInputChannel.close()
		scope.cancel()
	}

	protected abstract fun onAction(inMsg: T)

}

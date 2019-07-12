package com.hamperapp.actor

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


abstract class Actor<in T> {

	private val job = SupervisorJob()

	protected var scope = CoroutineScope(Dispatchers.IO + job)

	private lateinit var principalInputChannel: SendChannel<T>


	init {

		startPrincipalCoroutineActor()

	}

	fun send(inMsg: T) {

		scope.launch {

			principalInputChannel.send(inMsg)

		}

	}

	fun close() {

		onClose()

		principalInputChannel.close()

		scope.cancel()

	}

	protected fun startPrincipalCoroutineActor() {

		principalInputChannel = scope.actor {

			consumeEach {

				onAction(it)

			}

		}

	}

	protected open fun onClose() {}

	protected abstract fun onAction(inMsg: T)

}

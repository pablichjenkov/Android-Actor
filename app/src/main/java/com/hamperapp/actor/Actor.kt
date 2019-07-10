package com.hamperapp.actor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

abstract class Actor<T> {

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

	abstract fun onAction(inMsg: T)

}
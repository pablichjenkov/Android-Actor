package com.hamperapp.actor

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import java.util.concurrent.atomic.AtomicBoolean


abstract class Actor<in T> {

	private val job = SupervisorJob()

	protected var scope = CoroutineScope(Dispatchers.IO + job)

	private var inputChannel: SendChannel<T>? = null

	private var isStarted: AtomicBoolean = AtomicBoolean(false)


	open fun start() {

		if (isStarted.compareAndSet(false, true)) {

			startCoroutineActor()

		}

	}

	fun send(inMsg: T) {

		scope.launch {

			inputChannel?.send(inMsg)

		}

	}

	/**
	 * Common method to not repeat the cancelChildren() call in every subclass.
	 * It will cancel both child Actor-Coroutines the Principal and the Common.
	 * */
	open fun stop() {

		scope.coroutineContext.cancelChildren()

		isStarted.set(false)

	}

	open fun back() {}

	fun close() {

		onClose()

		inputChannel?.close()

		scope.cancel()

	}

	private fun startCoroutineActor() {

		inputChannel = scope.actor {

			consumeEach { msg ->

				onAction(msg)

			}

		}

	}

	protected open fun onClose() {}

	protected abstract fun onAction(inMsg: T)

}

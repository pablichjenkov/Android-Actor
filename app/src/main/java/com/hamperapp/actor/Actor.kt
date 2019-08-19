package com.hamperapp.actor

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import java.util.concurrent.atomic.AtomicBoolean


abstract class Actor<in T> {

	private val job = SupervisorJob()

	protected var scope = CoroutineScope(Dispatchers.IO + job)

	private var channel: Channel<T>? = null

	private var isStarted: AtomicBoolean = AtomicBoolean(false)


	open fun start() {

		if (isStarted.compareAndSet(false, true)) {

			startCoroutineActor()

		}

	}

	fun send(inMsg: T) {

		scope.launch {

			channel?.send(inMsg)

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

		channel?.close()

		scope.cancel()

	}

	private fun startCoroutineActor() {

		channel = Channel()

		scope.launch {

			channel?.consumeEach { msg ->

				onAction(msg)

			}

		}


	}

	protected open fun onClose() {}

	protected abstract fun onAction(inMsg: T)

}

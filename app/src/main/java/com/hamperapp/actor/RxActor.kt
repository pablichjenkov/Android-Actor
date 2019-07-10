package com.hamperapp.actor

import com.hamperapp.log.Logger
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.disposables.Disposable
import java.util.concurrent.ConcurrentLinkedQueue

@Deprecated("Use Kotlin Coroutine actor to be in sync with the community")
abstract class RxActor {

    private var sendPipe: FlowableEmitter<Any>? = null

    private val sendPipeDisposable: Disposable

    private val actionQueue = ConcurrentLinkedQueue<Any>()

    init {

        sendPipeDisposable = Flowable.create(
            { emitter: FlowableEmitter<Any> ->

                sendPipe = emitter

                send()

            },
            BackpressureStrategy.BUFFER
        ).subscribe(
            { action ->
                onAction(action)
            },
            { th ->
                Logger.e(th)
            }
        )

    }

    fun send(action: Any) {
        actionQueue.offer(action)
        send()
    }

    private fun send() {

        sendPipe?.run {

            var nextAction = actionQueue.poll()

            while (nextAction != null) {

                onNext(nextAction)

                nextAction = actionQueue.poll()
            }

        }

    }

    protected abstract fun onAction(action: Any)

    fun close() {
        sendPipeDisposable.dispose()
    }

}
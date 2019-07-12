package com.hamperapp.navigation

import androidx.fragment.app.Fragment
import com.hamperapp.UIActorMsg
import com.hamperapp.actor.BaseActor
import com.hamperapp.home.HomeActor
import com.hamperapp.launch.SplashActor
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch


class DrawerUIActor(
    private var parentUISendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>?
) : BaseActor<DrawerUIActor.InMsg>() {


    private var uiSendChannel: SendChannel<UIActorMsg>? = null

    private var childrenActor: List<BaseActor<*>>? = null

    private var activeActor: BaseActor<*>? = null

    lateinit var fragmentSink: SendChannel<UIActorMsg>


    override fun onCommonAction(commonMsg: BaseActor.InMsg) {

        when (commonMsg) {

            BaseActor.InMsg.OnStart -> {

                val titleMsg = UIActorMsg.SetTitle("Drawer Screen")

                val drawerFragment = DrawerFragment.newInstance(this)

                val uiMsg = UIActorMsg.SetFragment(drawerFragment, "drawerFragment")

                scope.launch {

                    parentUISendChannel.send(titleMsg)

                    parentUISendChannel.send(uiMsg)

                }

            }

            BaseActor.InMsg.OnStop -> {

                scope.coroutineContext.cancelChildren()

            }

            BaseActor.InMsg.OnBack -> {

                scope.launch {
                    // TODO(Pablo): Remove this logic and propagate the OnBack event to all the children
                    observerChannel?.send(OutMsg.OnDrawerComplete)

                    observerChannel = null

                    cancel()

                }

            }

        }

    }

    override fun onAction(inMsg: InMsg) {

        when (inMsg) {

            InMsg.View.OnViewReady -> {

                createUiActor()

                uiSendChannel?.run {

                    childrenActor = mutableListOf(
                        createHomeActor(this),
                        createSplashActor(this)
                    )

                    switchActor(0)
                }

            }

            InMsg.View.OnViewStop -> {}

            is InMsg.View.OnMenuItemSelected -> {

                switchActor(inMsg.position)

            }

        }

    }

    private fun createUiActor() {

        uiSendChannel = scope.actor {

            consumeEach { uiMsg ->

                when (uiMsg) {

                    is UIActorMsg.SetTitle -> {

                        parentUISendChannel.send(uiMsg)
                        // title = uiMsg.title

                    }

                    is UIActorMsg.SetFragment -> {

                        fragmentSink.send(uiMsg)
                        //renderBox.setView(uiMsg.fragment, uiMsg.id)

                    }

                    is UIActorMsg.SetView -> {

                        fragmentSink.send(uiMsg)
                        //renderBox.setView(uiMsg.view)

                    }

                    is UIActorMsg.BackResult -> {

                        // TODO(Pablo): Apply logic to always com back to the previous fragment
                        // as in a regular stack and only exit when Home is current.
                        // If a BackResult message indicates that no child Actor consumed the Back Pressed event.
                        // Then it is safe to finish our Activity now.
                        /*if (! uiMsg.consumed) {

                            mainActor.close()

                            uiSendChannel.close()

                            uiScope.cancel()

                            finish()

                        }*/

                    }

                }

            }

        }

    }

    // TODO(Pablo): DrawerActor should not know about its children Type or callbacks
    private fun createHomeActor(uiSendChannel: SendChannel<UIActorMsg>): BaseActor<*> {

        return HomeActor(
            uiSendChannel,
            scope.actor {

                consumeEach { msg ->

                    when (msg) {

                        HomeActor.OutMsg.OnHomeComplete -> {}

                    }

                }

            }
        )

    }

    // TODO(Pablo): DrawerActor should not know about its children Type or callbacks
    private fun createSplashActor(uiSendChannel: SendChannel<UIActorMsg>): BaseActor<*> {

        return SplashActor(
            uiSendChannel,
            scope.actor {

                consumeEach { splashMsg ->

                    when (splashMsg) {

                        SplashActor.OutMsg.OnSplashComplete -> {}

                    }

                }

            }
        )

    }

    private fun switchActor(position: Int) {

        scope.launch {

            //fragmentSink.send(OutMsg.View.Toast("position ${position} clicked"))

            childrenActor?.run {

                if (position < size) {

                    activeActor?.sendCommonMsg(BaseActor.InMsg.OnStop)

                    get(position).let { childActor ->

                        childActor.sendCommonMsg(BaseActor.InMsg.OnStart)

                        activeActor = childActor

                    }

                }

            }

        }

    }


    sealed class InMsg {

        sealed class View : InMsg() {

            object OnViewReady : View()

            object OnViewStop : View()

            class OnMenuItemSelected(val position: Int) : View()

        }

    }

    sealed class OutMsg {

        object OnDrawerComplete : OutMsg()


        sealed class View : OutMsg() {

            class SetFragment(val fragment: Fragment, val id: String) : View()

            class SetView(val view: android.view.View) : View()

            class Toast(val text: String) : View()

        }

    }

}

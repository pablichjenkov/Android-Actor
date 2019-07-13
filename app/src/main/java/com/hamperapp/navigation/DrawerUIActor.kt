package com.hamperapp.navigation

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import com.hamperapp.home.HomeActor
import com.hamperapp.launch.SplashActor
import com.hamperapp.settings.SettingsActor
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch


class DrawerUIActor(
    private var parentUISendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>?
) : Actor<DrawerUIActor.InMsg>() {


    private var uiSendChannel: SendChannel<UIActorMsg>? = null

    private lateinit var childrenActors: List<Actor<*>>

    private var activeActor: Actor<*>? = null

    lateinit var fragmentSink: SendChannel<UIActorMsg>


    override fun start() {
        super.start()

        val titleMsg = UIActorMsg.SetTitle("Drawer Screen")

        val drawerFragment = DrawerFragment.newInstance(this)

        val uiMsg = UIActorMsg.SetFragment(drawerFragment, "drawerFragment")

        scope.launch {

            parentUISendChannel.send(titleMsg)

            parentUISendChannel.send(uiMsg)

        }

    }

    override fun onAction(inMsg: InMsg) {

        when (inMsg) {

            InMsg.View.OnViewReady -> {

                createUiActor()

                uiSendChannel?.run {

                    childrenActors = mutableListOf(
                        createHomeActor(this),
                        createSplashActor(this),
                        createSettingsActor(this)
                    )

                    val menuItems: List<String> = childrenActors.map {
                        it.javaClass.simpleName
                    }

                    scope.launch {

                        fragmentSink.send(UIActorMsg.ShowNavigation(menuItems))

                    }

                    switchActor(0)

                }

            }

            InMsg.View.OnViewStop -> {}

            is InMsg.View.OnMenuItemSelected -> {

                switchActor(inMsg.position)

            }

        }

    }

    override fun back() {
        super.back()

        scope.launch {
            // TODO(Pablo): Remove this logic and propagate the OnBack event to all the children
            observerChannel?.send(OutMsg.OnDrawerComplete)

            observerChannel = null

            cancel()

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
    private fun createHomeActor(uiSendChannel: SendChannel<UIActorMsg>): Actor<*> {

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
    private fun createSplashActor(uiSendChannel: SendChannel<UIActorMsg>): Actor<*> {

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

    // TODO(Pablo): DrawerActor should not know about its children Type or callbacks
    private fun createSettingsActor(uiSendChannel: SendChannel<UIActorMsg>): Actor<*> {

        return SettingsActor(
            uiSendChannel,
            scope.actor {

                consumeEach { splashMsg ->

                    when (splashMsg) {

                        SettingsActor.OutMsg.OnSettingsComplete -> {}

                    }

                }

            }
        )

    }

    private fun switchActor(position: Int) {

        scope.launch {

            if (position < childrenActors.size) {

                activeActor?.stop()

                childrenActors[position].let { childActor ->

                    childActor.start()

                    activeActor = childActor

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

    }

}

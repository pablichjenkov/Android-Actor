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


class NavigationActor(
    private var parentUISendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>?
) : Actor<NavigationActor.InMsg>() {


    private var uiSendChannel: SendChannel<UIActorMsg>? = null

    private lateinit var childrenActors: List<Actor<*>>

    private var activeActor: Actor<*>? = null

    private lateinit var defaultActor: Actor<*>

    lateinit var fragmentSink: SendChannel<UIActorMsg>


    override fun start() {
        super.start()

        val titleMsg = UIActorMsg.SetTitle("Navigation Screen")

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

                    defaultActor = childrenActors[0]
                    setActiveActor(0)

                }

            }

            InMsg.View.OnViewStop -> {}

            is InMsg.View.OnMenuItemSelected -> {

                setActiveActor(inMsg.position)

            }

        }

    }

    override fun back() {

        activeActor?.back()

    }

    private fun createUiActor() {

        uiSendChannel = scope.actor {

            consumeEach { uiMsg ->

                when (uiMsg) {

                    is UIActorMsg.SetTitle -> {

                        parentUISendChannel.send(uiMsg)

                    }

                    is UIActorMsg.SetFragment -> {

                        fragmentSink.send(uiMsg)

                    }

                    is UIActorMsg.SetView -> {

                        fragmentSink.send(uiMsg)

                    }

                    is UIActorMsg.BackResult -> {

                        if (activeActor == defaultActor) {

                            scope.launch {

                                closeChildrenActor()

                                observerChannel?.send(OutMsg.OnDrawerComplete)

                                cancel()

                            }

                        }
                        else {

                            defaultActor = childrenActors[0]

                            setActiveActor(0)

                        }

                    }

                }

            }

        }

    }

    private fun createHomeActor(uiSendChannel: SendChannel<UIActorMsg>): Actor<*> {

        return HomeActor(
            uiSendChannel,
            null
        )

    }

    private fun createSplashActor(uiSendChannel: SendChannel<UIActorMsg>): Actor<*> {

        return SplashActor(
            uiSendChannel,
            null
        )

    }

    private fun createSettingsActor(uiSendChannel: SendChannel<UIActorMsg>): Actor<*> {

        return SettingsActor(
            uiSendChannel,
            null
        )

    }

    private fun setActiveActor(position: Int) {

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

    private fun closeChildrenActor() = childrenActors.forEach { it.close() }


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

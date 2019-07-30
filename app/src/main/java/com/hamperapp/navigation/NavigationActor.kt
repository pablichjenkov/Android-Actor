package com.hamperapp.navigation

import android.widget.Toast
import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import com.hamperapp.home.HomeActor
import com.hamperapp.order.OrderHistoryhActor
import com.hamperapp.promotion.PromotionActor
import com.hamperapp.settings.SettingsActor
import com.hamperapp.settings.SupportActor
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch


class NavigationActor(
    private var parentUISendChannel: SendChannel<UIActorMsg>
) : Actor<NavigationActor.InMsg>() {


    private var uiSendChannel: SendChannel<UIActorMsg>? = null

    private lateinit var childrenActors: List<Actor<*>>

    private var activeActor: Actor<*>? = null

    private lateinit var defaultActor: Actor<*>

    var parentChannel: SendChannel<OutMsg>? = null

    lateinit var fragmentChannel: SendChannel<UIActorMsg>


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

                if (activeActor != null) return

                createUiActor()

                uiSendChannel?.run {

                    childrenActors = mutableListOf(
                        createHomeActor(this),
                        createReferFriendActor(this),
                        createOrderHistoryActor(this),
                        createSettingsActor(this),
                        createSupportActor(this)
                    )

                    val menuItems: List<String> = listOf(
                        "HOME",
                        "REFER A FRIEND",
                        "MY ORDERS",
                        "MY PREFERENCES",
                        "SUPPORT"
                    )

                    scope.launch {

                        fragmentChannel.send(UIActorMsg.ShowNavigation(menuItems))

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

                        //parentUISendChannel.send(uiMsg)

                        fragmentChannel.send(uiMsg)

                    }

                    is UIActorMsg.SetFragment -> {

                        fragmentChannel.send(uiMsg)

                    }

                    is UIActorMsg.SetView -> {

                        fragmentChannel.send(uiMsg)

                    }

                    is UIActorMsg.Toast -> {

                        fragmentChannel.send(uiMsg)

                    }

                    is UIActorMsg.BackResult -> {

                        if (activeActor == defaultActor) {

                            activeActor = null

                            scope.launch {

                                closeChildrenActor()

                                parentChannel?.send(OutMsg.OnDrawerComplete)

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

    private fun createReferFriendActor(uiSendChannel: SendChannel<UIActorMsg>): Actor<*> {

        return PromotionActor(
            uiSendChannel,
            null
        )

    }

    private fun createOrderHistoryActor(uiSendChannel: SendChannel<UIActorMsg>): Actor<*> {

        return OrderHistoryhActor(
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

    private fun createSupportActor(uiSendChannel: SendChannel<UIActorMsg>): Actor<*> {

        return SupportActor(
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

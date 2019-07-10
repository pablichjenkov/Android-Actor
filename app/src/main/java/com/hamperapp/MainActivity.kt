package com.hamperapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class MainActivity : AppCompatActivity() {

    val uiScope = CoroutineScope(Dispatchers.Main)

    private lateinit var renderBox: RenderContextDefault

    private lateinit var mainActor: MainActor

    private lateinit var uiSendChannel: SendChannel<UIActorMsg>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        renderBox = RenderContextDefault(supportFragmentManager, frameBox)

        createUiActor()

        mainActor = MainActor(uiSendChannel)

    }

    override fun onResume() {
        super.onResume()
        mainActor.send(MainActor.InMsg.OnStart)
    }

    override fun onPause() {
        super.onPause()
        mainActor.send(MainActor.InMsg.OnStop)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mainActor.send(MainActor.InMsg.OnBack)
    }

    private fun createUiActor() {

        uiSendChannel = uiScope.actor {

            consumeEach { uiMsg ->

                when (uiMsg) {

                    is UIActorMsg.SetTitle -> {

                        title = uiMsg.title

                    }

                    is UIActorMsg.SetView -> {

                        renderBox.setView(uiMsg.fragment, uiMsg.id)

                    }

                    is UIActorMsg.PushView -> {

                        renderBox.setView(uiMsg.fragment, uiMsg.id)

                    }

                    is UIActorMsg.PopView -> {

                        //renderBox.popView()

                    }

                }

            }

        }

    }

}

sealed class UIActorMsg {

    class SetTitle(val title: String) : UIActorMsg()

    class SetView(val fragment: Fragment, val id: String) : UIActorMsg()

    class PushView(val fragment: Fragment, val id: String) : UIActorMsg()

    object PopView : UIActorMsg()

}

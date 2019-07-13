package com.hamperapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class MainActivity : AppCompatActivity() {

    private val uiScope = CoroutineScope(Dispatchers.Main)

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

    override fun onStart() {
        super.onStart()
        mainActor.start()
    }

    override fun onStop() {
        super.onStop()
        mainActor.stop()
    }

    /**
     * We never called super.onBackPressed() method. We propagate the Back pressed event to children Actors. Later on,
     * if we receive a BackResult consumed event, then we finish this Activity.
     */
    override fun onBackPressed() {
        mainActor.back()
    }

    private fun createUiActor() {

        uiSendChannel = uiScope.actor {

            consumeEach { uiMsg ->

                when (uiMsg) {

                    is UIActorMsg.SetTitle -> { title = uiMsg.title }

                    is UIActorMsg.SetFragment -> {

                        renderBox.setView(uiMsg.fragment, uiMsg.id)

                    }

                    is UIActorMsg.SetView -> {

                        renderBox.setView(uiMsg.view)

                    }

                    is UIActorMsg.Toast -> {

                        Toast.makeText(this@MainActivity, uiMsg.message, Toast.LENGTH_SHORT).show()

                    }

                    is UIActorMsg.BackResult -> {

                        // If a BackResult message indicates that no child Actor consumed the Back Pressed event.
                        // Then it is safe to finish our Activity now.
                        if (! uiMsg.consumed) {

                            mainActor.close()

                            uiSendChannel.close()

                            uiScope.cancel()

                            finish()

                        }

                    }

                    is UIActorMsg.ShowNavigation -> {}

                }

            }

        }

    }

}

sealed class UIActorMsg {

    class SetTitle(val title: String) : UIActorMsg()

    class SetFragment(val fragment: Fragment, val id: String) : UIActorMsg()

    class SetView(val view: View) : UIActorMsg()

    class Toast(val message: String) : UIActorMsg()

    class BackResult(val consumed: Boolean) : UIActorMsg()

    class ShowNavigation(val navItems: List<String>) : UIActorMsg()

}

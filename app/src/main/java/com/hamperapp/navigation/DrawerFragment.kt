package com.hamperapp.navigation

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hamperapp.R
import com.hamperapp.RenderContextDefault
import com.hamperapp.UIActorMsg
import kotlinx.android.synthetic.main.fragment_drawer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class DrawerFragment : Fragment() {

	private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

	private lateinit var mailboxChannel: SendChannel<UIActorMsg>

	private lateinit var actor: DrawerUIActor

	private lateinit var renderBox: RenderContextDefault

	private lateinit var drawerAdapter: DrawerAdapter


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		mailboxChannel = fragmentCoroutineScope.actor {

			consumeEach { msg ->

				when (msg) {

					is UIActorMsg.SetFragment -> {

						renderBox.setView(msg.fragment, msg.id)

					}

					is UIActorMsg.SetView -> {

						renderBox.setView(msg.view)

					}

					is UIActorMsg.Toast -> {

						Toast.makeText(context, msg.message, Toast.LENGTH_SHORT).show()

					}

					is UIActorMsg.ShowNavigation -> {

						setupNavigationRecycler(msg.navItems)

					}

				}

			}

		}

		actor.fragmentSink = mailboxChannel

	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {

		return inflater.inflate(R.layout.fragment_drawer, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		renderBox = RenderContextDefault(childFragmentManager, drawerBox)

	}

	override fun onStart() {
		super.onStart()

		actor.send(DrawerUIActor.InMsg.View.OnViewReady)

	}

	override fun onStop() {
		super.onStop()

		actor.send(DrawerUIActor.InMsg.View.OnViewStop)

		fragmentCoroutineScope.coroutineContext.cancelChildren()

	}

	private fun setupNavigationRecycler(navItems: List<String>) {

		drawerAdapter = DrawerAdapter(navItems) { position ->

			actor.send(DrawerUIActor.InMsg.View.OnMenuItemSelected(position))

			drawerLayout.closeDrawer(Gravity.RIGHT)

		}

		navigationRecycler.layoutManager = LinearLayoutManager(context)

		navigationRecycler.adapter = drawerAdapter

	}

	companion object {

		@JvmStatic
		fun newInstance(actor: DrawerUIActor) =

			DrawerFragment().apply {

				this.actor = actor

			}

	}

}

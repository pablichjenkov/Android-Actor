package com.hamperapp.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.hamperapp.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import kotlinx.android.synthetic.main.fragment_collection.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class CollectionFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var actor: CollectionActor<CollectionActor.InMsg.View>

    private lateinit var itemAdapter: ItemAdapter<GenericItem>

    private lateinit var fastAdapter: FastAdapter<GenericItem>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
    }

    override fun onStart() {
        super.onStart()

        actor.fragmentChannel = fragmentCoroutineScope.actor {

            consumeEach { event ->

                when (event) {

                    CollectionActor.OutMsg.View.OnLoad -> {

                    }

                    is CollectionActor.OutMsg.View.OnUpdate -> {

                        itemAdapter.add(event.itemList.items)

                    }

                    CollectionActor.OutMsg.View.OnError -> {

                    }

                }

            }

        }

        actor.send(CollectionActor.InMsg.View.OnViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(CollectionActor.InMsg.View.OnViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()

    }

    private fun setupAdapter() {

        itemAdapter = ItemAdapter()

        fastAdapter = FastAdapter.with(itemAdapter)

        //val selectExtension = fastAdapter.getSelectExtension()

        //selectExtension.isSelectable = true

        fastAdapter.setHasStableIds(true)

        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.itemAnimator = DefaultItemAnimator()

        recyclerView.adapter = fastAdapter

    }

    companion object {

        @JvmStatic
        fun newInstance(actor: CollectionActor<CollectionActor.InMsg.View>) =

            CollectionFragment().apply {

                this.actor = actor

            }

    }

}

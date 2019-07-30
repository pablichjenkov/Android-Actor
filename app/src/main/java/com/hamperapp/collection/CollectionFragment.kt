package com.hamperapp.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hamperapp.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.select.getSelectExtension
import kotlinx.android.synthetic.main.fragment_collection.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import java.util.ArrayList


class CollectionFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var actor: CollectionActor<CollectionActor.InMsg>

    private lateinit var headerAdapter: ItemAdapter<GenericItem>

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

                    CollectionActor.OutMsg.View.OnSuccess -> {

                        generateProducts()

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

        headerAdapter = ItemAdapter()

        itemAdapter = ItemAdapter()

        fastAdapter = FastAdapter.with(listOf(headerAdapter, itemAdapter))

        val selectExtension = fastAdapter.getSelectExtension()

        selectExtension.isSelectable = true

        fastAdapter.setHasStableIds(true)


        //val layoutManager = LinearLayoutManager(context)

        val layoutManager = GridLayoutManager(context, 2)


        recyclerView.layoutManager = layoutManager

        recyclerView.itemAnimator = DefaultItemAnimator()

        recyclerView.adapter = fastAdapter

    }

    private fun generateProducts() {

        //fill with some sample data
        val item = SimpleCell().withName("Header")
        item.identifier = 1
        headerAdapter.add(item)

        val items = ArrayList<GenericItem>()

        for (i in 1..20) {

            val simpleItem = SimpleCell().withName("Test $i").withHeader(headers[i / 5])

            simpleItem.identifier = (100 + i).toLong()

            items.add(simpleItem)

            items.add(SimpleCell1())
        }

        itemAdapter.add(items)
    }

    companion object {

        @JvmStatic
        fun newInstance(actor: CollectionActor<CollectionActor.InMsg>) =

            CollectionFragment().apply {

                this.actor = actor

            }

        private val headers = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")

    }

}

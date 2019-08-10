package com.hamperapp.collection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hamperapp.R
import com.hamperapp.log.Logger
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.ISelectionListener
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import kotlinx.android.synthetic.main.fragment_collection.recyclerView
import kotlinx.android.synthetic.main.fragment_collection_parallax.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import java.util.*


class ParallaxCollectionFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var actor: ParallaxCollectionActor<ParallaxCollectionActor.InMsg.View>

    private lateinit var itemAdapter: ItemAdapter<GenericItem>

    private lateinit var fastAdapter: FastAdapter<GenericItem>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collection_parallax, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()

        bottomFrame.setOnClickListener {

            actor.send(ParallaxCollectionActor.InMsg.View.OnBottomViewClick)

        }

    }

    override fun onStart() {
        super.onStart()

        actor.fragmentChannel = fragmentCoroutineScope.actor {

            consumeEach { event ->

                when (event) {

                    ParallaxCollectionActor.OutMsg.View.OnLoad -> {

                    }

                    is ParallaxCollectionActor.OutMsg.View.OnUpdate -> {

                        itemAdapter.add(event.itemList.items)

                    }

                    ParallaxCollectionActor.OutMsg.View.OnError -> {

                    }

                }

            }

        }

        actor.send(ParallaxCollectionActor.InMsg.View.OnViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(ParallaxCollectionActor.InMsg.View.OnViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()

    }

    private fun setupAdapter() {

        itemAdapter = ItemAdapter()

        fastAdapter = FastAdapter.with(itemAdapter)

        val selectExtension = fastAdapter.getSelectExtension()

        selectExtension.isSelectable = true

        selectExtension.multiSelect = true

        selectExtension.allowDeselection = true

        selectExtension.selectionListener = object: ISelectionListener<GenericItem> {

            override fun onSelectionChanged(item: GenericItem?, selected: Boolean) {

                Logger.d("onSelectionChanged: ${item?.isSelected}")

            }

        }

        fastAdapter.setHasStableIds(true)

        //val layoutManager = LinearLayoutManager(context)

        val layoutManager = GridLayoutManager(context, 2)


        recyclerView.layoutManager = layoutManager

        recyclerView.itemAnimator = DefaultItemAnimator()

        recyclerView.adapter = fastAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            var mHasReachedBottomOnce = false

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (RecyclerView.SCROLL_STATE_IDLE == newState) {

                    // direction integers:
                    // Negative distance for up,
                    // Positive distance for down,
                    // 0 will always return false.

                    if (! recyclerView.canScrollVertically(16) &&! mHasReachedBottomOnce) {

                        mHasReachedBottomOnce = true

                        bottomFrame.visibility = View.INVISIBLE

                    }

                } else if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {

                    if (mHasReachedBottomOnce) {

                        mHasReachedBottomOnce = false

                        bottomFrame.visibility = View.VISIBLE

                    }

                }

            }

        })

    }


    companion object {

        @JvmStatic
        fun newInstance(actor: ParallaxCollectionActor<ParallaxCollectionActor.InMsg.View>) =

            ParallaxCollectionFragment().apply {

                this.actor = actor

            }

    }

}

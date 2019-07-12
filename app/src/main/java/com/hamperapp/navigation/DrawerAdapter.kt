package com.hamperapp.navigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hamperapp.R
import kotlinx.android.synthetic.main.cell_drawer.view.*


class DrawerAdapter(
	private val navMenuItems: List<String>,
	private val clickCallback: (Int) -> Unit
) : RecyclerView.Adapter<DrawerAdapter.VH>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

		val celView
				= LayoutInflater.from(parent.context)
				.inflate(R.layout.cell_drawer, parent, false)

		return VH(celView)

	}

	override fun getItemCount(): Int {
		return navMenuItems.size
	}

	override fun onBindViewHolder(holder: VH, position: Int) {

		val title = navMenuItems[position]

		holder.itemView.itemTitle.text = title

		holder.itemView.setOnClickListener {
			clickCallback(position)
		}

	}

	class VH(view: View) : RecyclerView.ViewHolder(view)

}
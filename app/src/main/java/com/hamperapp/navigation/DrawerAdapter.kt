package com.hamperapp.navigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hamperapp.R
import kotlinx.android.synthetic.main.cell_drawer.view.*


class DrawerAdapter(
	val clickCallback: (Int) -> Unit
) : RecyclerView.Adapter<DrawerAdapter.VH>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

		val celView
				= LayoutInflater.from(parent.context)
				.inflate(R.layout.cell_drawer, parent, false)

		return VH(celView)

	}

	override fun getItemCount(): Int {
		return 10
	}

	override fun onBindViewHolder(holder: VH, position: Int) {

		val title = "Option $position"

		holder.itemView.itemTitle.text = title

		holder.itemView.setOnClickListener {
			clickCallback(position)
		}

	}

	class VH(view: View) : RecyclerView.ViewHolder(view)

}
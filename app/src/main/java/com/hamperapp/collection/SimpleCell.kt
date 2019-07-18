package com.hamperapp.collection

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import com.hamperapp.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.drag.IDraggable
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.ui.utils.FastAdapterUIUtils
import com.mikepenz.materialize.holder.StringHolder
import com.mikepenz.materialize.util.UIUtils


open class SimpleCell : AbstractItem<SimpleCell.ViewHolder>(), IDraggable {

    var header: String? = null
    var name: StringHolder? = null
    var description: StringHolder? = null

    override var isDraggable = true

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.cell_simple_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.cell_simple

    fun withHeader(header: String): SimpleCell {
        this.header = header
        return this
    }

    fun withName(Name: String): SimpleCell {
        this.name = StringHolder(Name)
        return this
    }

    fun withName(@StringRes NameRes: Int): SimpleCell {
        this.name = StringHolder(NameRes)
        return this
    }

    fun withDescription(description: String): SimpleCell {
        this.description = StringHolder(description)
        return this
    }

    fun withDescription(@StringRes descriptionRes: Int): SimpleCell {
        this.description = StringHolder(descriptionRes)
        return this
    }

    fun withIdentifier(identifier: Long): SimpleCell {
        this.identifier = identifier
        return this
    }

    fun withIsDraggable(draggable: Boolean): SimpleCell {
        this.isDraggable = draggable
        return this
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(private var view: View) : FastAdapter.ViewHolder<SimpleCell>(view) {
        var name: TextView = view.findViewById(R.id.name)
        var description: TextView = view.findViewById(R.id.description)

        override fun bindView(cell: SimpleCell, payloads: MutableList<Any>) {
            //get the context
            val ctx = itemView.context

            //set the background for the cell
            UIUtils.setBackground(view, FastAdapterUIUtils.getSelectableBackground(ctx, Color.RED, true))
            //set the text for the name
            StringHolder.applyTo(cell.name, name)
            //set the text for the description or hide
            StringHolder.applyToOrHide(cell.description, description)
        }

        override fun unbindView(cell: SimpleCell) {
            name.text = null
            description.text = null
        }
    }

}

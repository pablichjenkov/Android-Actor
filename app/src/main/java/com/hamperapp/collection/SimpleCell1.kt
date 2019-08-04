package com.hamperapp.collection

import android.content.Context
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


open class SimpleCell1 : AbstractItem<SimpleCell1.ViewHolder>(), IDraggable {

    var header: String? = "Cell 1 Header"
    var name: String? = "Cell 1 Name"
    var description: StringHolder? = null

    private lateinit var context: Context

    private val viewContext by lazy { context }

    override var isDraggable = true

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.cell_simple_1_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.cell_product_info

    fun withHeader(header: String): SimpleCell1 {
        this.header = header
        return this
    }

    fun withName(Name: String): SimpleCell1 {
        this.name = Name
        return this
    }

    fun withName(@StringRes NameRes: Int): SimpleCell1 {
        this.name = context.getString(NameRes)//StringHolder(NameRes)
        return this
    }

    fun withDescription(description: String): SimpleCell1 {
        this.description = StringHolder(description)
        return this
    }

    fun withDescription(@StringRes descriptionRes: Int): SimpleCell1 {
        this.description = StringHolder(descriptionRes)
        return this
    }

    fun withIdentifier(identifier: Long): SimpleCell1 {
        this.identifier = identifier
        return this
    }

    fun withIsDraggable(draggable: Boolean): SimpleCell1 {
        this.isDraggable = draggable
        return this
    }

    override fun getViewHolder(v: View): ViewHolder {
        this.context = v.context
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(private var view: View) : FastAdapter.ViewHolder<SimpleCell1>(view) {
        var name: TextView = view.findViewById(R.id.cost)
        var description: TextView = view.findViewById(R.id.title)

        override fun bindView(cell: SimpleCell1, payloads: MutableList<Any>) {
            //get the context
            val ctx = itemView.context

            //set the background for the cell
            UIUtils.setBackground(view, FastAdapterUIUtils.getSelectableBackground(ctx, Color.RED, true))
            //set the text for the name
            name.text = cell.name//StringHolder.applyTo(cell.name, name)
            //set the text for the description or hide
            StringHolder.applyToOrHide(cell.description, description)
        }

        override fun unbindView(cell: SimpleCell1) {
            name.text = null
            description.text = null
        }
    }

}

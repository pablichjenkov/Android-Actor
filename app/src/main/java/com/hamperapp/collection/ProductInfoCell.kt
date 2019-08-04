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


open class ProductInfoCell : AbstractItem<ProductInfoCell.ViewHolder>(), IDraggable {

    var currentViewHolder: ViewHolder? = null

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
        get() = R.id.cell_product_info_type

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.cell_product_info

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        currentViewHolder = holder

    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)

        currentViewHolder = null
    }

    fun withHeader(header: String): ProductInfoCell {
        this.header = header
        return this
    }

    fun withName(Name: String): ProductInfoCell {
        this.name = StringHolder(Name)
        return this
    }

    fun withName(@StringRes NameRes: Int): ProductInfoCell {
        this.name = StringHolder(NameRes)
        return this
    }

    fun withDescription(description: String): ProductInfoCell {
        this.description = StringHolder(description)
        return this
    }

    fun withDescription(@StringRes descriptionRes: Int): ProductInfoCell {
        this.description = StringHolder(descriptionRes)
        return this
    }

    fun withIdentifier(identifier: Long): ProductInfoCell {
        this.identifier = identifier
        return this
    }

    fun withIsDraggable(draggable: Boolean): ProductInfoCell {
        this.isDraggable = draggable
        return this
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(private var view: View) : FastAdapter.ViewHolder<ProductInfoCell>(view) {
        var name: TextView = view.findViewById(R.id.cost)
        var description: TextView = view.findViewById(R.id.title)



        override fun bindView(cell: ProductInfoCell, payloads: MutableList<Any>) {
            //get the context
            val ctx = itemView.context

            //set the background for the cell
            UIUtils.setBackground(view, FastAdapterUIUtils.getSelectableBackground(ctx, Color.RED, true))
            //set the text for the name
            StringHolder.applyTo(cell.name, name)
            //set the text for the description or hide
            StringHolder.applyToOrHide(cell.description, description)
        }

        override fun unbindView(cell: ProductInfoCell) {
            name.text = null
            description.text = null
        }
    }

}

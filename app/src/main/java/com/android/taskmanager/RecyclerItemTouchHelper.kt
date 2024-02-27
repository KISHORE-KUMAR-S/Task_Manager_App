package com.android.taskmanager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.taskmanager.adapter.TodoAdapter

class RecyclerItemTouchHelper(
    private val context: Context,
    private val adapter: TodoAdapter
) : SimpleCallback(0, LEFT or RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        target: ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (direction == LEFT) {
            AlertDialog.Builder(context)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this Task?")
                .setPositiveButton("Confirm") { _, _ ->
                    adapter.deleteItem(position)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    adapter.notifyItemChanged(viewHolder.adapterPosition)
                }
                .create()
                .show()
        } else {
            adapter.editItem(position)
        }
    }

    override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder, dX : Float, dY : Float, actionState : Int, isCurrentlyActive : Boolean) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val icon : Drawable
        val background : ColorDrawable

        val itemView : View = viewHolder.itemView
        val backgroundCornerOffset = 20

        if(dX > 0) {
            icon = ContextCompat.getDrawable(context, R.drawable.edit)!!
            background = ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        } else {
            icon = ContextCompat.getDrawable(context, R.drawable.delete)!!
            background = ColorDrawable(Color.RED)
        }

        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        if(dX > 0) {
            val iconLeft = itemView.left + iconMargin
            val iconRight = itemView.left + iconMargin + icon.intrinsicHeight
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom)
        }
        else if(dX < 0) {
            val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
            val iconRight = itemView.right - iconMargin
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

            background.setBounds(itemView.right + dX.toInt() - backgroundCornerOffset, itemView.top, itemView.right, itemView.bottom)
        }
        else {
            background.setBounds(0,0,0,0)
        }
        background.draw(canvas)
        icon.draw(canvas)
    }



}
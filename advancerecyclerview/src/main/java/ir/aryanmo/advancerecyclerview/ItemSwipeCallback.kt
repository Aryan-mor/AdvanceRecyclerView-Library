package ir.aryanmo.advancerecyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper


class ItemSwipeCallback(private val mSwipeItemListener: OnSwipeItemListener, swipeDir: Int = LEFT_AND_RIGHT_DIR) :
    ItemTouchHelper.SimpleCallback(0, swipeDir) {
    private var leftIcon: Drawable? = null
    private var leftBackground: ColorDrawable? = null
    private var rightIcon: Drawable? = null
    private var rightBackground: ColorDrawable? = null

    companion object {
        const val LEFT_DIR = ItemTouchHelper.LEFT
        const val RIGHT_DIR = ItemTouchHelper.RIGHT
        const val LEFT_AND_RIGHT_DIR = LEFT_DIR or RIGHT_DIR
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        // used for up and down movements
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val position = viewHolder.adapterPosition
        if (i == LEFT_DIR) {
            mSwipeItemListener.onSwipeToLeft(position)
            return
        }
        if (i == RIGHT_DIR) {
            mSwipeItemListener.onSwipeToRight(position)
            return
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20

        if (dX > 0) { // Swiping to the right
            rightIcon?.let {
                val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                val iconBottom = iconTop + it.intrinsicHeight

                val iconLeft = itemView.left + iconMargin + it.intrinsicWidth
                val iconRight = itemView.left + iconMargin
                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                rightBackground?.let { bg ->
                    bg.setBounds(
                        itemView.left, itemView.top,
                        itemView.left + dX.toInt() + backgroundCornerOffset,
                        itemView.bottom
                    )
                    bg.draw(c)
                }

                it.draw(c)
            }
        }

        if (dX < 0) { // Swiping to the left
            leftIcon?.let {
                val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                val iconBottom = iconTop + it.intrinsicHeight

                val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                leftIcon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)


                leftBackground?.let { bg ->
                    bg.setBounds(
                        itemView.right + dX.toInt() - backgroundCornerOffset,
                        itemView.top, itemView.right, itemView.bottom
                    )
                    bg.draw(c)
                }
                it.draw(c)
            }
        }

//        if (dX == 0f){
//
//        }
    }

    private fun a() {
//
//        val iconMargin = (itemView.height - leftIcon!!.intrinsicHeight) / 2
//        val iconTop = itemView.top + (itemView.height - leftIcon!!.intrinsicHeight) / 2
//        val iconBottom = iconTop + leftIcon!!.intrinsicHeight
//
//        when {
//
//
//            dX > 0 -> { // Swiping to the right
//                val iconLeft = itemView.left + iconMargin + leftIcon!!.intrinsicWidth
//                val iconRight = itemView.left + iconMargin
//                leftIcon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//
//                leftBackground!!.setBounds(
//                    itemView.left, itemView.top,
//                    itemView.left + dX.toInt() + backgroundCornerOffset,
//                    itemView.bottom
//                )
//            }
//            dX < 0 -> { // Swiping to the left
//                val iconLeft = itemView.right - iconMargin - leftIcon!!.intrinsicWidth
//                val iconRight = itemView.right - iconMargin
//                leftIcon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//
//                leftBackground!!.setBounds(
//                    itemView.right + dX.toInt() - backgroundCornerOffset,
//                    itemView.top, itemView.right, itemView.bottom
//                )
//            }
//            else -> {// view is unSwiped
//                leftBackground!!.setBounds(0, 0, 0, 0)
//            }
//        }
//
//        leftBackground!!.draw(c)
//        leftIcon!!.draw(c)

    }

    private fun b() {
//          if (dX > 0) { // Swiping to the right
//            rightIcon?.let {
//                val iconMargin = (itemView.height - it.intrinsicHeight) / 2
//                val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
//                val iconBottom = iconTop + it.intrinsicHeight
//
//                val iconLeft = itemView.left + iconMargin + it.intrinsicWidth
//                val iconRight = itemView.left + iconMargin
//                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//
//                rightBackground?.let { bg ->
//                    bg.setBounds(
//                        itemView.left, itemView.top,
//                        itemView.left + dX.toInt() + backgroundCornerOffset,
//                        itemView.bottom
//                    )
//                    bg.draw(c)
//                }
//
//                it.draw(c)
//            }
//        }
//
//        if (dX < 0) { // Swiping to the left
//            leftIcon?.let {
//                val iconMargin = (itemView.height - it.intrinsicHeight) / 2
//                val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
//                val iconBottom = iconTop + it.intrinsicHeight
//
//                val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
//                val iconRight = itemView.right - iconMargin
//                leftIcon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//
//
//                leftBackground?.let { bg ->
//                    bg.setBounds(
//                        itemView.right + dX.toInt() - backgroundCornerOffset,
//                        itemView.top, itemView.right, itemView.bottom
//                    )
//                    bg.draw(c)
//                }
//                it.draw(c)
//            }
//        }
//
//        if (dX == 0f){
//
//        }
    }

    fun setupSwipeLeftLayout(leftIcon: Drawable, leftBackground: ColorDrawable): ItemSwipeCallback {
        this.leftIcon = leftIcon
        this.leftBackground = leftBackground
        return this
    }

    fun setupSwipeLeftLayout(
        context: Context,
        leftIcon: Drawable, @ColorRes leftBackgroundRes: Int
    ): ItemSwipeCallback {
        this.leftIcon = leftIcon
        this.leftBackground = ColorDrawable(ContextCompat.getColor(context, leftBackgroundRes))
        return this
    }

    fun setupSwipeRightLayout(rightIcon: Drawable, rightBackground: ColorDrawable): ItemSwipeCallback {
        this.rightIcon = rightIcon
        this.rightBackground = rightBackground
        return this
    }

    fun hasLeftLayout(): Boolean {
        return leftIcon != null
    }

    fun hasRightLayout(): Boolean {
        return rightIcon != null
    }


    interface OnSwipeItemListener {
        fun onSwipeToLeft(position: Int)
        fun onSwipeToRight(position: Int)
    }
}

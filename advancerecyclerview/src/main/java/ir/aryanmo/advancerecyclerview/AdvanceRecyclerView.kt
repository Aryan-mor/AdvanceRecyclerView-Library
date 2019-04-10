package ir.aryanmo.advancerecyclerview

import android.content.Context
import android.content.res.TypedArray
import android.support.annotation.AnimRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils


open class AdvanceRecyclerView : RecyclerView {
    var orientation = 0
        set(value) {
            if (value == VERTICAL || value == HORIZONTAL) {
                field = value
                linearLayoutManager.orientation = value
                layoutManager = linearLayoutManager
                return
            }
            logError(
                "AdvanceRecycleView::setOrientation()",
                Exception("setOrientation value must be 0(AdvanceRecycleView.VERTICAL),1(AdvanceRecycleView.HORIZONTAL)")
            )
        }
    @LayoutRes
    var itemView = -1
    @LayoutRes
    var loadingItemView = -1
        protected set
    private var itemCount = 0

    fun setItemCount(itemCount: Int, notifyDataSetChange: Boolean = true) {
        this.itemCount = itemCount - 1
        if (notifyDataSetChange) {
            myAdapter.notifyDataSetChanged()
        }
    }

    lateinit var linearLayoutManager: LinearLayoutManager
        protected set
    var isInfinite = false
    var onAdapterListener: OnAdapterListener? = null


    //Pagination
    var onPaginationLoadingAdapterListener: OnPaginationLoadingAdapterListener? = null
    var paginationListener: OnPaginationListener? = null
        protected set
    var isPaginationLoadingNextPage = false
        set(value) {
            field = value
            myAdapter.notifyDataSetChanged()
        }

    protected val VIEW_TYPE_ITEM = 0
    protected val VIEW_TYPE_LOADING = 1
    protected var advanceViewType = VIEW_TYPE_ITEM
    var isInitialize = false

    //Swipe
    var itemSwipeCallback: ItemSwipeCallback? = null

    //Animation
    var itemAnimRes: Int = -1
    private var lastItemPosition = -1


    val count: Int
        get() = itemCount + 1

    //Functionality

    //    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    var myAdapter: RecyclerView.Adapter<*> = object : RecyclerView.Adapter<ViewHolder>() {
        override fun getItemViewType(position: Int): Int {

            advanceViewType =
                if (!isInfinite && isPaginationLoadingNextPage && itemCount == position + 1) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
            return advanceViewType
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (advanceViewType == VIEW_TYPE_ITEM) {
                if (itemView != -1) {
                    val view = LayoutInflater.from(parent.context).inflate(itemView, parent, false)
                    return ViewHolder(view)
                }
                return ViewHolder(onAdapterListener!!.onCreateViewHolder()!!)
            }
            if (loadingItemView != -1) {
                val view = LayoutInflater.from(parent.context).inflate(loadingItemView, parent, false)
                return ViewHolder(view)
            }
            return ViewHolder(onPaginationLoadingAdapterListener!!.onLoadingCreateViewHolder())
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var position = position

            if (itemAnimRes != -1) {
                val animation = AnimationUtils.loadAnimation(context, itemAnimRes)
                holder.itemView.startAnimation(animation)
                lastItemPosition = position
            }

            if (advanceViewType == VIEW_TYPE_ITEM) {
                if (onAdapterListener == null)
                    return
                if (isInfinite) {
                    position = getItemPositionOnInfinity(position)
                }
                onAdapterListener!!.onBindViewHolder(holder, position)
                return
            }
            if (onPaginationLoadingAdapterListener == null)
                return
            onPaginationLoadingAdapterListener!!.onLoadingBindViewHolder(holder, position)
        }

        override fun getItemCount(): Int {
            if (isInfinite)
                return Integer.MAX_VALUE
            return if (!isInfinite && isPaginationLoadingNextPage && onPaginationLoadingAdapterListener != null) count + 1 else count
        }

    }

    val firstItemPasOnInfinity: Int
        get() {
            val mid = Integer.MAX_VALUE / 2
            return mid - mid % count
        }

    constructor(context: Context) : super(context) {
        setDefaultProperties()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setDefaultProperties()
        setAttributes(attrs)
    }

    fun init(
        @LayoutRes itemView: Int, itemCount: Int = this.itemCount,
        linearLayoutManager: LinearLayoutManager? = null
    ) {
        var linearLayoutManager = linearLayoutManager
        this.itemView = itemView
        linearLayoutManager ?: let {
            linearLayoutManager = LinearLayoutManager(context)
        }
        init(itemCount)
    }

    fun init(itemCount: Int) {

        setItemCount(itemCount)
        adapter = myAdapter
        setCurrentPosition(0)

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                processPagination(dx, dy)
            }
        })

        itemSwipeCallback?.let {
            ItemTouchHelper(it).attachToRecyclerView(this)
        }
        isInitialize = true
    }

    protected fun processPagination(dx: Int, dy: Int) {
        if (paginationListener == null)
            return

        if (linearLayoutManager.childCount + linearLayoutManager.findFirstVisibleItemPosition() >= layoutManager!!.itemCount) {
            paginationListener!!.onEndItemListener(this, dx, dy)
            return
        }

        if (!canScrollVertically(1)) {
            paginationListener!!.onEndItemListener(this, dx, dy)
            return
        }
    }

    protected fun setDefaultProperties() {
        linearLayoutManager = LinearLayoutManager(context)
        layoutManager = linearLayoutManager
        setHasFixedSize(true)

        if (isInfinite)
            scrollToPos(firstItemPasOnInfinity)


    }

    protected fun setAttributes(attrs: AttributeSet?) {
        setOrientationFromAttr(attrs)
    }

    protected fun setOrientationFromAttr(attr: AttributeSet?) {
        val a = getStyledAttributes(attr)
        val or = a.getInt(R.styleable.AdvanceRecyclerView_ar_adre_orientation, -1)
        if (or != -1)
            orientation = or

        a.recycle()
    }

    protected fun getStyledAttributes(attr: AttributeSet?): TypedArray {
        return context.theme.obtainStyledAttributes(
            attr, R.styleable.AdvanceRecyclerView, 0, 0
        )
    }

    // PROPERTIES

    fun notifyDataSetChanged(newItemCount: Int) {
        addItemCount(newItemCount)
    }

    fun notifyItemInsert(newItemPosition: Int, scrollToItem: Boolean = true) {
        myAdapter.notifyItemInserted(newItemPosition)
        setItemCount(count + 1, false)
        if (scrollToItem) {
            smoothScrollToPos(newItemPosition)
        }
        Log.e("Ari", "notifyItemInsert -> $itemCount")
    }

    fun notifyItemRemove(itemPosition: Int) {
        Log.e("Ari", "notifyItemInsert before remove -> $itemCount")
        myAdapter.notifyItemRemoved(itemPosition)
        setItemCount(count - 1, false)
        Log.e("Ari", "notifyItemInsert before after -> $itemCount")
    }

    fun addNewItemCount(newItemCount: Int) {
        addItemCount(newItemCount + count)
    }

    fun addItemCount(itemCount: Int) {
        setItemCount(itemCount)
    }

    fun getItemPositionOnInfinity(position: Int): Int {
        return position % count
    }

    fun scrollToPos(pos: Int) {
        //        lm.scrollToPosition(pos);
        scrollToPosition(pos)
    }

    fun smoothScrollToPos(pos: Int) {
        smoothScrollToPosition(pos)
        //        lm.smoothScrollToPosition(this, null, pos);
    }

    fun setItemAnimation(@AnimRes animRes: Int = android.R.anim.slide_in_left) {
        itemAnimRes = animRes
    }

    fun enableItemAnim() {
        setItemAnimation(android.R.anim.slide_in_left)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    //Listener
    interface OnAdapterListener {
        fun onCreateViewHolder(): View? {
            return null
        }

        fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }
    }

    interface OnPaginationLoadingAdapterListener {
        fun onLoadingCreateViewHolder(): View

        fun onLoadingBindViewHolder(holder: ViewHolder, position: Int)
    }

    interface OnPaginationListener {
        fun onEndItemListener(recyclerView: AdvanceRecyclerView, dx: Int, dy: Int)
    }

    interface OnItemChangedListener

    fun setPaginationLoadingItemView(@LayoutRes loadingItemView: Int) {
        this.loadingItemView = loadingItemView
    }

    fun setOnPaginationListener(onPaginationListener: OnPaginationListener) {
        this.paginationListener = onPaginationListener
    }

    fun setCurrentPosition(position: Int) {
        if (isInfinite) {
            scrollToPos(firstItemPasOnInfinity + position)
            return
        }
        scrollToPos(position)
    }

    fun findLastVisibleItemPosition(): Int {
        return linearLayoutManager.findLastVisibleItemPosition()
    }

    //Swipe
    fun setSwipeListener(itemSwipeCallback: ItemSwipeCallback) {
        this.itemSwipeCallback = itemSwipeCallback
        if (!itemSwipeCallback.hasLeftLayout() && !itemSwipeCallback.hasRightLayout()) {
            throw Exception("ItemSwipeCallback must by setup left or right layout")
        }
        if (isInitialize) {
            ItemTouchHelper(itemSwipeCallback).attachToRecyclerView(this)
        }
    }

    private fun logError(title: String, e: Exception) {
        Log.e("AdvanceRecycleView", "AdvanceRecycleView::" + title + "   Error -> " + e.message)
    }

    companion object {
        val VERTICAL = LinearLayoutManager.VERTICAL
        val HORIZONTAL = LinearLayoutManager.HORIZONTAL
    }
}
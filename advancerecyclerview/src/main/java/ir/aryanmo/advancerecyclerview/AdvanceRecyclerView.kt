package ir.aryanmo.advancerecyclerview

import android.content.Context
import android.content.res.TypedArray
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlin.collections.ArrayList
import android.graphics.Color
import java.util.*

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
    //Getter and Setter

    @LayoutRes
    var itemView = -1
    @LayoutRes
    var loadingItemView = -1
        protected set
    var itemCount = 0
        set(value) {
            field = value - 1
            myAdapter.notifyDataSetChanged()
        }
    lateinit var linearLayoutManager: LinearLayoutManager
        protected set

    var isInfinite = false
    protected var itemListener: OnListItemListener? = null
    var onAdapterListener: OnAdapterListener? = null
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
    protected var erisViewType = VIEW_TYPE_ITEM


    val count: Int
        get() = itemCount + 1

    //Functionality

    //    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    var myAdapter: RecyclerView.Adapter<*> = object : RecyclerView.Adapter<ViewHolder>() {
        override fun getItemViewType(position: Int): Int {

            erisViewType =
                if (!isInfinite && isPaginationLoadingNextPage && itemCount == position + 1) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
            return erisViewType
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (erisViewType == VIEW_TYPE_ITEM) {
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
            if (erisViewType == VIEW_TYPE_ITEM) {
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

    fun init(@LayoutRes itemView: Int, itemCount: Int, linearLayoutManager: LinearLayoutManager?) {
        var linearLayoutManager = linearLayoutManager
        this.itemView = itemView
        if (linearLayoutManager == null) {
            linearLayoutManager = LinearLayoutManager(context)
        }
        this.linearLayoutManager = linearLayoutManager
        init(itemCount)
    }

    fun init(itemCount: Int) {

        Log.e("Ari","init itemCount -> $itemCount")
        this.itemCount = itemCount
        Log.e("Ari","init this.itemCount -> ${this.itemCount}")
        adapter = myAdapter
        setCurrentPosition(0)

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                processPagination(dx, dy)
            }
        })
    }

    protected fun processPagination(dx: Int, dy: Int) {
        if (paginationListener == null)
            return

        Log.e("Aryan", " item pos -> " + linearLayoutManager.findFirstCompletelyVisibleItemPosition())


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
        val or = a.getInt(R.styleable.AdvanceRecyclerView_ar_orientation, -1)
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


    fun addNewItemCount(newItemCount: Int) {
        Log.e("Ari","new count -> $newItemCount")
        Log.e("Ari","itemCount -> $itemCount")
        addItemCount(newItemCount + itemCount + 1)
    }

    fun addItemCount(itemCount: Int) {

        Log.e("Ari","final -> $itemCount")
        this.itemCount = itemCount
        myAdapter.notifyDataSetChanged()
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

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return super.onTouchEvent(e)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    //Listener
    interface OnAdapterListener {
        fun onCreateViewHolder(): View?

        fun onBindViewHolder(holder: ViewHolder, position: Int)
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

    private fun logError(title: String, e: Exception) {
        Log.e("AdvanceRecycleView", "AdvanceRecycleView::" + title + "   Error -> " + e.message)
    }

    companion object {
        val VERTICAL = LinearLayoutManager.VERTICAL
        val HORIZONTAL = LinearLayoutManager.HORIZONTAL
    }
}
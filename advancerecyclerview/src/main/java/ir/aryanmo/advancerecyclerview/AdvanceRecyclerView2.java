package ir.aryanmo.advancerecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class AdvanceRecyclerView2 extends RecyclerView {
    final public static int VERTICAL = LinearLayoutManager.VERTICAL;
    final public static int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    protected int orientation = 0;
    @LayoutRes
    protected int itemView = -1;
    @LayoutRes
    protected int loadingItemView = -1;
    protected int itemCount = 0;
    protected LinearLayoutManager lm;
    protected boolean infinite = false;
    protected OnListItemListener itemListener = null;
    protected OnAdapterListener onAdapterListener;
    protected OnPaginationLoadingAdapterListener onPaginationLoadingAdapterListener = null;
    protected OnPaginationListener paginationListener = null;
    protected boolean isPaginationLoadingNextPage = false;
    protected final int VIEW_TYPE_ITEM = 0;
    protected final int VIEW_TYPE_LOADING = 1;
    protected int erisViewType = VIEW_TYPE_ITEM;

    public AdvanceRecyclerView2(Context context) {
        super(context);
        setDefaultProperties();
    }

    public AdvanceRecyclerView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDefaultProperties();
        setAttributes(attrs);
    }

    public void init(@LayoutRes int itemView, int itemCount, @Nullable LinearLayoutManager linearLayoutManager) {
        this.itemView = itemView;
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(getContext());
        }
        this.lm = linearLayoutManager;
        init(itemCount);
    }

    public void init(int itemCount) {
        this.itemCount = itemCount - 1;
        setAdapter(adapter);
        setCurrentPosition(0);

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                processPagination(dx, dy);
            }
        });
    }

    protected void processPagination(int dx, int dy) {
        if (paginationListener == null)
            return;

        Log.e("Aryan" , " item pos -> "+ getLinearLayoutManager().findFirstCompletelyVisibleItemPosition());


        if ((getLinearLayoutManager().getChildCount() + getLinearLayoutManager().findFirstVisibleItemPosition()) >= getLayoutManager().getItemCount()) {
            paginationListener.onEndItemListener(this, dx, dy);
            return;
        }

        if (!canScrollVertically(1)) {
            paginationListener.onEndItemListener(this, dx, dy);
            return;
        }
    }

    protected void setDefaultProperties() {
        lm = new LinearLayoutManager(getContext());
        setLayoutManager(lm);
        setHasFixedSize(true);

        if (infinite)
            scrollToPos(getFirstItemPasOnInfinity());
    }

    protected void setAttributes(AttributeSet attrs) {
        setOrientationFromAttr(attrs);
    }

    protected void setOrientationFromAttr(AttributeSet attr) {
        TypedArray a = getStyledAttributes(attr);
        int or = a.getInt(R.styleable.AdvanceRecyclerView_ar_orientation, -1);
        if (or != -1)
            setOrientation(or);

        a.recycle();
    }

    protected TypedArray getStyledAttributes(AttributeSet attr) {
        return getContext().getTheme().obtainStyledAttributes(
                attr, R.styleable.AdvanceRecyclerView, 0, 0);
    }

    // PROPERTIES

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        if (orientation == VERTICAL || orientation == HORIZONTAL) {
            this.orientation = orientation;
            lm.setOrientation(orientation);
            setLayoutManager(lm);
            return;
        }
       logError("AdvanceRecycleView::setOrientation()", new Exception("setOrientation value must be 0(AdvanceRecycleView.VERTICAL),1(AdvanceRecycleView.HORIZONTAL)"));
    }


    public int getCount() {
        return itemCount + 1;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount - 1;
        adapter.notifyDataSetChanged();
    }

    public void addNewItemCount(int newItemCount) {
        addItemCount(itemCount += newItemCount - 1);
    }

    public void addItemCount(int itemCount) {
        this.itemCount = itemCount - 1;
        adapter.notifyDataSetChanged();
    }

    //Functionality

    //    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    protected Adapter adapter = new Adapter<ViewHolder>() {
        @Override
        public int getItemViewType(int position) {

            erisViewType = !isInfinite() && isPaginationLoadingNextPage() && getItemCount() == (position + 1) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
            return erisViewType;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (erisViewType == VIEW_TYPE_ITEM) {
                if (itemView != -1) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(itemView, parent, false);
                    return new ViewHolder(view);
                }
                return new ViewHolder(onAdapterListener.onCreateViewHolder());
            }
            if (loadingItemView != -1) {
                View view = LayoutInflater.from(parent.getContext()).inflate(loadingItemView, parent, false);
                return new ViewHolder(view);
            }
            return new ViewHolder(onPaginationLoadingAdapterListener.onLoadingCreateViewHolder());
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (erisViewType == VIEW_TYPE_ITEM) {
                if (onAdapterListener == null)
                    return;
                if (isInfinite()) {
                    position = getItemPositionOnInfinity(position);
                }
                onAdapterListener.onBindViewHolder(holder, position);
                return;
            }
            if (onPaginationLoadingAdapterListener == null)
                return;
            onPaginationLoadingAdapterListener.onLoadingBindViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            if (isInfinite())
                return Integer.MAX_VALUE;
            if (!isInfinite() && isPaginationLoadingNextPage() && onPaginationLoadingAdapterListener != null)
                return getCount() + 1;
            return getCount();
        }

    };

    public int getItemPositionOnInfinity(int position) {
        return position % getCount();
    }

    public int getFirstItemPasOnInfinity() {
        int mid = Integer.MAX_VALUE / 2;
        return mid - (mid % getCount());
    }

    public void scrollToPos(int pos) {
//        lm.scrollToPosition(pos);
        scrollToPosition(pos);
    }

    public void smoothScrollToPos(int pos) {
        smoothScrollToPosition(pos);
//        lm.smoothScrollToPosition(this, null, pos);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    //Listener
    public interface OnAdapterListener {
        @Nullable View onCreateViewHolder();

        void onBindViewHolder(ViewHolder holder, int position);
    }

    public interface OnPaginationLoadingAdapterListener {
        View onLoadingCreateViewHolder();

        void onLoadingBindViewHolder(ViewHolder holder, int position);
    }

    public interface OnPaginationListener {
        void onEndItemListener(AdvanceRecyclerView2 recyclerView, int dx, int dy);
    }

    public interface OnItemChangedListener {
    }
    //Getter and Setter

    public int getItemView() {
        return itemView;
    }

    public void setItemView(@LayoutRes int itemView) {
        this.itemView = itemView;
    }

    public int getLoadingItemView() {
        return loadingItemView;
    }

    public void setPaginationLoadingItemView(@LayoutRes int loadingItemView) {
        this.loadingItemView = loadingItemView;
    }

    public OnAdapterListener getOnAdapterListener() {
        return onAdapterListener;
    }

    public void setOnAdapterListener(OnAdapterListener onAdapterListener) {
        this.onAdapterListener = onAdapterListener;
    }

    public OnPaginationLoadingAdapterListener getOnPaginationLoadingAdapterListener() {
        return onPaginationLoadingAdapterListener;
    }

    public void setOnPaginationLoadingAdapterListener(OnPaginationLoadingAdapterListener onLoadingAdapterListener) {
        this.onPaginationLoadingAdapterListener = onLoadingAdapterListener;
    }

    public OnPaginationListener getPaginationListener() {
        return paginationListener;
    }

    public void setOnPaginationListener(OnPaginationListener onPaginationListener) {
        this.paginationListener = onPaginationListener;
    }

    public LinearLayoutManager getLinearLayoutManager() {
        return lm;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }

    public void setCurrentPosition(int position) {
        if (infinite) {
            scrollToPos(getFirstItemPasOnInfinity() + position);
            return;
        }
        scrollToPos(position);
    }

    public int findLastVisibleItemPosition() {
        return lm.findLastVisibleItemPosition();
    }

    public boolean isPaginationLoadingNextPage() {
        return isPaginationLoadingNextPage;
    }

    public void setPaginationLoadingNextPage(boolean paginationLoadingNextPage) {
        isPaginationLoadingNextPage = paginationLoadingNextPage;
        adapter.notifyDataSetChanged();
    }

    private void logError(String title,Exception e){
        Log.e("AdvanceRecycleView","AdvanceRecycleView::" + title + "   Error -> " + e.getMessage());
    }
}

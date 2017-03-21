package me.foji.smartui;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedList;

/**
 * 支持下拉刷新，上拉加载更多的RecyclerView
 *
 * @author Scott Smith 2017-03-17 11:05
 */
public class SuperRecyclerView extends RelativeLayout {
    /**
     * 垂直方向
     */
    public static final int VERTICAL = 0;
    /**
     * 水平方向
     */
    public static final int HORIZONTAL = 1;

    private int mOrientation = VERTICAL;
    // 标记是否正在进行下拉刷新，上拉加载更多操作
    private boolean isDraging = false;
    private int mTouchSlop;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    // Refresh view
    private BaseRefreshView mRefreshView;
    // Loading view
    private BaseLoadingView mLoadingView;
    // 当前状态
    private State mState;
    // 滑动模式
    private Mode mMode = Mode.getDefault();
    private float mInitialMotionX , mInitialMotionY;
    private float mLastMotionX , mLastMotionY;

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {}

    public SuperRecyclerView(Context context) {
        this(context , null);
    }

    public SuperRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        
        mRefreshView = new DefaultRefreshView(context);
        addView(mRecyclerView);

        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mLoadingView = new  DefaultLoadingView(context);
        addView(mLoadingView);
        initListener();
    }

    private void initListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /**
     * Set the {@link android.support.v7.widget.RecyclerView.LayoutManager} that this RecyclerView
     * will use.
     *
     * <p>In contrast to other adapter-backed views such as {@link android.widget.ListView}
     * or {@link android.widget.GridView}, RecyclerView allows client code to provide custom
     * layout arrangements for child views. These arrangements are controlled by the
     * {@link android.support.v7.widget.RecyclerView.LayoutManager}. A LayoutManager must be
     * provided for RecyclerView to function.</p>
     *
     * <p>Several default strategies are provided for common uses such as lists and grids.</p>
     *
     * @param layoutManager LayoutManager to use
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
        mLayoutManager = layoutManager;

        if(layoutManager instanceof LinearLayoutManager) {
            if(LinearLayoutManager.HORIZONTAL ==
                    ((LinearLayoutManager) layoutManager).getOrientation()) {
                mOrientation = HORIZONTAL;
            } else {
                mOrientation = VERTICAL;
            }
        }

        if(layoutManager instanceof GridLayoutManager) {
            mOrientation = VERTICAL;
        }

        if(layoutManager instanceof StaggeredGridLayoutManager) {
            if(StaggeredGridLayoutManager.HORIZONTAL ==
                    ((StaggeredGridLayoutManager) layoutManager).getOrientation()) {
                mOrientation = HORIZONTAL;
            } else {
                mOrientation = VERTICAL;
            }
        }

        if(layoutManager instanceof SuperLayoutManager) {
            if(SuperLayoutManager.HORIZONTAL ==
                    ((SuperLayoutManager) layoutManager).getOrientation()) {
                mOrientation = HORIZONTAL;
            } else {
                mOrientation = VERTICAL;
            }
        }
    }

    /**
     * 设置布局排列方向
     *
     * @param orientation 传入{@link #VERTICAL}或{@link #HORIZONTAL}
     */
    public void setOrientation(@OrientationMode int orientation) {
        mOrientation = orientation;
    }

    // 是否已经滑动到最底端，准备开始上拉加载操作
    private boolean isReadyForPullUp() {
        int lastItemPosition = mRecyclerView.getChildCount() - 1;
        View lastItemView = mRecyclerView.getChildAt(lastItemPosition);
        int lastVisiblePosition = mRecyclerView.getChildLayoutPosition(lastItemView);
        // 是否是最后一个Item View
        if (lastVisiblePosition >= 0 && lastVisiblePosition >= mRecyclerView.getAdapter().getItemCount() - 1) {
            return mRecyclerView.getChildAt(lastItemPosition).getBottom() <= mRecyclerView.getBottom();
        }
        return false;
    }

    // 是否已经滑动到最顶端，准备开始下拉操作
    private boolean isReadyForPullDown() {
        if (mRecyclerView.getChildCount() <= 0) {
            return true;
        }
        int firstVisiblePosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        if (firstVisiblePosition == 0) {
            return mRecyclerView.getChildAt(0).getTop() == mRecyclerView.getPaddingTop();
        }
        return false;
    }

    private boolean isReadyForPull() {
        switch (mMode) {
            case PULL_DOWN: {
                return isReadyForPullDown();
            }
            case PULL_UP: {
                return isReadyForPullUp();
            }
            case BOTH: {
                return isReadyForPullDown() || isReadyForPullUp();
            }
            default:
                return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mMode.isDisable()) return false;

        int action = ev.getAction();
        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            isDraging = false;
            return false;
        }

        if(action != MotionEvent.ACTION_DOWN && isDraging) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if(isReadyForPull()) {
                    mLastMotionX = mInitialMotionX = ev.getX();
                    mLastMotionX = mInitialMotionY = ev.getY();
                    isDraging = false;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if(isReadyForPull()) {
                    final float x = ev.getX() , y = ev.getY();
                    final float diff , oppositeDiff , absDiff;

                    switch (mOrientation) {
                        case HORIZONTAL: {
                            diff = x - mLastMotionX;
                            oppositeDiff = y - mLastMotionY;
                            break;
                        }

                        case VERTICAL:
                            default: {
                            diff = y - mLastMotionY;
                            oppositeDiff = x - mLastMotionX;
                            break;
                        }
                    }

                    absDiff = Math.abs(diff);

                    if(absDiff > mTouchSlop && absDiff > Math.abs(oppositeDiff)) {
                        mLastMotionX = x;
                        mLastMotionY = y;
                        isDraging = true;
                    } else {

                    }

                    mLastMotionX = x;
                    mLastMotionY = y;
                }
                break;
            }
        }

        return isDraging;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public enum Mode {
        /**
         * 关闭下拉刷新和上拉加载
         */
        DISABLE(0x0),
        /**
         * 只开启下拉刷新
          */
        PULL_DOWN(0x1),
        /**
         * 只开启上拉加载更多
         */
        PULL_UP(0x2),
        /**
         * 下拉刷新，上拉加载更多功能均开启
         */
        BOTH(0x4);

        private int mIntValue;

        Mode(int intValue) {
            mIntValue = intValue;
        }

        static Mode mapIntToValue(int modeInt) {
            for(Mode value : Mode.values()) {
                if(modeInt == value.getIntValue()) {
                    return value;
                }
            }

            return getDefault();
        }

        public int getIntValue() {
            return mIntValue;
        }

        public static Mode getDefault() {
            return BOTH;
        }

        // 允许下拉刷新
        public boolean permitsPullToRefresh() {
            return this != DISABLE && this != PULL_UP;
        }

        // 允许进行上拉加载更多
        public boolean permitsPullToLoadMore() {
            return this != DISABLE && this != PULL_DOWN;
        }

        // 状态是否关闭
        public boolean isDisable() {
            return this == DISABLE;
        }
    }

    public enum State {
        RESET(0x0),
        PULL_TO_REFRESH(0x1),
        RELEASE_TO_REFRESH(0x2),
        REFRESHING(0x3),
        LOADING_MORE(0x4);

        static State mapIntToValue(int stateInt) {
            for(State value : State.values()) {
                if(stateInt == value.getIntValue()) {
                    return value;
                }
            }

            return RESET;
        }

        private int mIntValue;

        State(int intValue) {
            mIntValue = intValue;
        }

        int getIntValue() {
            return mIntValue;
        }
    }
}

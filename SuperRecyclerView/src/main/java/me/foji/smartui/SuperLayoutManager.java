package me.foji.smartui;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于支持下拉刷新，上拉加载更多的LayoutManager
 *
 * @author Scott Smith 2017-03-17 14:11
 */
public abstract class SuperLayoutManager extends RecyclerView.LayoutManager {
    /**
     * 垂直排列
     */
    public static final int VERTICAL = 0;
    /**
     * 水平排列
     */
    public static final int HORIZONTAL = 1;

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {}

    /**
     * 获取布局排列方向
     *
     * @return 排列方向
     */
    public abstract int getOrientation();

    /**
     * 设置布局排列方向 {@link VER}
     *
     * @param orientation
     */
    public abstract void setOrientation(@OrientationMode int orientation);
}

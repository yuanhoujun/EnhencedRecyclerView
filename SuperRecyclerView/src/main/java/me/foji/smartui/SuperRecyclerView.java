package me.foji.smartui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 请描述使用该类使用方法！！！
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

    private int mOrientation = HORIZONTAL;


    public SuperRecyclerView(Context context) {
        super(context);
    }

    public SuperRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public enum ORIENTATION {
        VERTICAL ,
        HORIZONTAL
    }
}

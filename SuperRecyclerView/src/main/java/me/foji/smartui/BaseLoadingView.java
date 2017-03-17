package me.foji.smartui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Base loading view , implemented by {@link RelativeLayout}
 *
 * @author Scott Smith 2017-03-17 11:17
 */
public abstract class BaseLoadingView extends RelativeLayout {
    public BaseLoadingView(Context context) {
        super(context);
    }

    public BaseLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void onLoadMore(PullIndictor indictor);
}

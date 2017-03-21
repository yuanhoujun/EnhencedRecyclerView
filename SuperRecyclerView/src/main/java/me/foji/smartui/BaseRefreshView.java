package me.foji.smartui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Base refresh view , implemented by {@link RelativeLayout}
 *
 * @author Scott Smith 2017-03-17 11:16
 */
public abstract class BaseRefreshView extends RelativeLayout {


    public BaseRefreshView(Context context) {
        this(context , null);
    }

    public BaseRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

    }

    public abstract void onRefresh(PullIndictor indictor);

    public abstract void onRelease(PullIndictor indictor);
}

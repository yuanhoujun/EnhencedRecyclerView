package me.foji.smartui;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * 请描述使用该类使用方法！！！
 *
 * @author Scott Smith 2017-03-17 14:50
 */
public class DefaultRefreshView extends BaseRefreshView {
    private ProgressBar mProgressBar;

    public DefaultRefreshView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mProgressBar = new ProgressBar(context);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT ,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mProgressBar.setLayoutParams(lp);

        addView(mProgressBar);
    }

    @Override
    public void onRefresh(PullIndictor indictor) {

    }

    @Override
    public void onRelease(PullIndictor indictor) {

    }
}

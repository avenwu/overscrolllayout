package net.avenwu.overscrolllayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by aven on 4/21/16.
 */
public class OverScrollWebView extends WebView implements ScrollIntercept {
    private static final String TAG = OverScrollWebView.class.getSimpleName();
    ScrollOver mLayout;
    int mScrolledY;

    public OverScrollWebView(Context context) {
        super(context);
    }

    public OverScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OverScrollWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void attach(ScrollOver layout) {
        mLayout = layout;
    }

    @Override
    public boolean canChildScrollUp(ScrollOver layout) {
        return (int) Math.max(0, Math.floor(getContentHeight() * getScale())
                - (getHeight() - getPaddingBottom() - getPaddingTop())) > 0;
    }

    @Override
    public void tryScrollToTop(ScrollOver layout) {
        log("tryScrollToTop:" + mScrolledY);
        if (mScrolledY != 0) {
            mLayout.smooth2Top();
            mScrolledY = 0;
        }
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        log("overScrollBy: deltaX=" + deltaX + ", deltaY=" + deltaY + ", scrollX=" + scrollX + ", scrollRangeX=" + scrollRangeX);
        final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

        if (mLayout != null) {
            mScrolledY += deltaY;
            mLayout.scrollY(-deltaY);
        }
        return returnValue;
    }

    private void log(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg);
        }
    }
}

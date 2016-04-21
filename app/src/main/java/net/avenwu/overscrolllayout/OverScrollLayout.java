package net.avenwu.overscrolllayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.Scroller;

/**
 * Created by aven on 4/20/16.
 */
public class OverScrollLayout extends ViewGroup implements ScrollOver {
    public static final String TAG = OverScrollLayout.class.getSimpleName();
    private View mForegroundView;
    private View mBackgroundView;
    private Scroller mScroller;
    private int mTouchSlop;
    float mX;
    float mY;
    float mOverPercent = 0.2f;
    float mMaxOverPercent = 0.5f;
    int mHeightLimitMax;
    boolean isSliding = false;

    public OverScrollLayout(Context context) {
        super(context);
        init();
    }

    public OverScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OverScrollLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        log("onMeasure: count=" + count);
        if (count > 1) {
            mForegroundView = getChildAt(1);
            mBackgroundView = getChildAt(0);
        } else {
            mForegroundView = getChildAt(0);
        }
        if (mBackgroundView != null) {
            measureChildWithMargins(mBackgroundView, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        if (mForegroundView != null) {
            measureChildWithMargins(mForegroundView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            mHeightLimitMax = (int) (mForegroundView.getMeasuredHeight() * mMaxOverPercent);
            if (mForegroundView instanceof ScrollIntercept) {
                ((ScrollIntercept) mForegroundView).attach(this);
            }
        }
        if (mForegroundView != null && mBackgroundView != null) {
            mForegroundView.bringToFront();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mBackgroundView != null) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) mBackgroundView.getLayoutParams();
            int left = layoutParams.leftMargin + getPaddingLeft();
            int top = layoutParams.topMargin + getPaddingTop();
            int right = left + mBackgroundView.getMeasuredWidth();
            int bottom = top + mBackgroundView.getMeasuredHeight();
            mBackgroundView.layout(left, top, right, bottom);
            log("onLayout, mBackgroudView = " + mBackgroundView.toString() +
                    ", l=" + left + ",t=" + top + ", r=" + right + ",b=" + bottom);
        }
        if (mForegroundView != null) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) mForegroundView.getLayoutParams();
            int left = layoutParams.leftMargin + getPaddingLeft();
            int top = layoutParams.topMargin + getPaddingTop();
            int right = left + mForegroundView.getMeasuredWidth();
            int bottom = top + mForegroundView.getMeasuredHeight();
            mForegroundView.layout(left, top, right, bottom);
            log("onLayout, mForegroundView = " + mForegroundView.toString() +
                    ", l=" + left + ",t=" + top + ", r=" + right + ",b=" + bottom);
        }
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        if (getChildCount() < 2) {
            super.addView(child, index, params);
        } else {
            throw new IllegalArgumentException("OverscrollLayout can add only two child views");
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        log("onInterceptTouchEvent:" + ev.toString());
        if (!isEnabled() || canChildScrollUp()) {
            //make child layout to determine the scroll state
            if (mForegroundView instanceof ScrollIntercept &&
                    (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP)) {
                log("try tryScrollToTop");
                ((ScrollIntercept) mForegroundView).tryScrollToTop(this);
            }
            return false;
        }
        final int action = ev.getAction();
        if (action != MotionEvent.ACTION_DOWN && isSliding) return true;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isSliding = false;
                mX = ev.getX();
                mY = ev.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isSliding = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = ev.getY() - mY;
                log("onInterceptTouchEvent, canChildScrollUp=" + canChildScrollUp());
                if (dy > mTouchSlop && !isSliding) {
                    mY = ev.getY();
                    mX = ev.getX();
                    isSliding = true;
                    log("dy=" + dy);
                }
                break;

        }
        log("onInterceptTouchEvent, isSliding=" + isSliding);
        return isSliding;
    }

    public boolean canChildScrollUp() {
        if (mForegroundView instanceof ScrollIntercept) {
            return ((ScrollIntercept) mForegroundView).canChildScrollUp(this);
        } else if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mForegroundView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mForegroundView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mForegroundView, -1) || mForegroundView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mForegroundView, -1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        log("onTouchEvent:" + event.toString());
        if (!isEnabled() || canChildScrollUp()) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                isSliding = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
            case MotionEvent.ACTION_MOVE:
                float offsetX = event.getX() - mX;
                float offsetY = event.getY() - mY;
                log("move, offsetX=" + offsetX + ", offsetY=" + offsetY + ", mTouchSlop=" + mTouchSlop);
                if (isSliding) {
                    if (offsetY > 0) {
                        scrollY((int) offsetY);
                        mX = event.getX();
                        mY = event.getY();
                    } else {
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                int currentY = mForegroundView.getTop();
                int gap = (int) (mForegroundView.getHeight() * mOverPercent);
                if (currentY + event.getY() - mY >= gap / 2) {
                    int duration = (int) (Math.abs(0 - currentY + 0.5f) / mForegroundView.getHeight() * 1000);
                    log("duration=" + duration);
                    mScroller.startScroll(0, currentY, 0, 0 - currentY, duration);
                } else {
                    int duration = (int) (Math.abs(-gap - currentY + 0.5f) / mForegroundView.getHeight() * 1000);
                    log("duration=" + duration);
                    mScroller.startScroll(0, currentY, 0, -gap - currentY, duration);
                }
                isSliding = false;
                invalidate();
                return false;
        }
        return true;
    }

    @Override
    public void scrollY(int offsetY) {
        if (mForegroundView != null) {
            if (mForegroundView.getTop() + offsetY > mHeightLimitMax) {
                offsetY = mHeightLimitMax - mForegroundView.getTop();
            }
            mForegroundView.offsetTopAndBottom(offsetY);
        }
    }

    @Override
    public void smooth2Top() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        int currentY = mForegroundView.getTop();
        log("from " + currentY);
        int duration = (int) (Math.abs(currentY + 0.5f) / mForegroundView.getHeight() * 1000);
        mScroller.startScroll(0, currentY, 0, -currentY, duration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int oldTop = mForegroundView.getTop();
            int top = mScroller.getCurrY();
            if (oldTop != mScroller.getFinalY()) {
                //top min limit is 0
                int offsetY = Math.max(top, 0) - oldTop;
                mForegroundView.offsetTopAndBottom(offsetY);
            }
            invalidate();
        } else {
            mScroller.abortAnimation();
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    private void log(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg);
        }
    }
}

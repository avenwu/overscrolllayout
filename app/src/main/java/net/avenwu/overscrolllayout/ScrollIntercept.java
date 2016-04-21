package net.avenwu.overscrolllayout;

/**
 * Created by aven on 4/21/16.
 */
public interface ScrollIntercept {
    void attach(ScrollOver layout);

    boolean canChildScrollUp(ScrollOver layout);

    void tryScrollToTop(ScrollOver layout);
}

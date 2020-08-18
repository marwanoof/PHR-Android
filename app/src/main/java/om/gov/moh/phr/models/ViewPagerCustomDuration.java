package om.gov.moh.phr.models;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;

import androidx.viewpager.widget.ViewPager;

public class ViewPagerCustomDuration extends ViewPager {

    private ScrollerCustomDuration mScroller = null;

    public ViewPagerCustomDuration(Context context) {
        super(context);
        scrollInitViewPager();
    }

    public ViewPagerCustomDuration(Context context, AttributeSet attrs) {
        super(context, attrs);
        scrollInitViewPager();
    }

    private void scrollInitViewPager() {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);
            mScroller = new ScrollerCustomDuration(getContext(), (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
        }
    }
}
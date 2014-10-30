package com.zandbee.vstrokova.floatingtitlebartest;

import android.animation.ObjectAnimator;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zandbee.vstrokova.floatingtitlebartest.ObservableScrollView.Callbacks;

/**
 * Created by Veronika Strokova on 27.10.2014.
 */
public class FloatingTitleBarActivity extends ActionBarActivity implements Callbacks {

    protected ObservableScrollView scrollView;
    protected ImageView imageView;
    protected TextView titleView;
    protected TextView bodyTextView;
    protected LinearLayout bodyLayout;
    protected ImageButton joinButton;
    protected Toolbar toolbar;
    protected View titleBackground;
    protected FrameLayout titleLayout;

    protected ProgressDrawable pd;
    protected int statusBarHeight;
    protected int imageViewHeight;
    protected int titleViewHeight;
    protected int titleLayoutHeight;
    protected int toolbarHeight;
    protected int joinButtonCenterHeight;
    protected int[] location = new int[2];

    private boolean titleInUpperPosition = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        final View root = getLayoutInflater().inflate(R.layout.activity_floating_title_bar, null);
        setContentView(root);

        scrollView = (ObservableScrollView) root.findViewById(R.id.floating_scroll);
        scrollView.addCallbacks(this);

        imageView = (ImageView) root.findViewById(R.id.floating_img);

        bodyTextView = (TextView) root.findViewById(R.id.test_text);
        bodyLayout = (LinearLayout) root.findViewById(R.id.body_layout);
        joinButton = (ImageButton) root.findViewById(R.id.join_group_button);

        statusBarHeight = getStatusBarHeight();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleView = (TextView) findViewById(R.id.floating_title_bar_text);

        titleLayout = (FrameLayout) findViewById(R.id.title_layout);

        titleBackground = findViewById(R.id.title_background_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            imageViewHeight = imageView.getHeight();
            titleViewHeight = titleView.getHeight();
            titleLayoutHeight = titleLayout.getHeight();
            toolbarHeight = toolbar.getHeight();
            joinButtonCenterHeight = joinButton.getHeight() / 2;
            //titleView.setTranslationY(imageView.getBottom() - titleViewHeight);
            titleLayout.setTranslationY(imageView.getBottom() - titleViewHeight - toolbarHeight);
            joinButton.setTranslationY(imageView.getBottom() - joinButtonCenterHeight);

            final LayoutParams layoutParams = titleBackground.getLayoutParams();
            layoutParams.height = titleViewHeight + toolbarHeight;
            titleBackground.setLayoutParams(layoutParams);

            // get color with Picasso
            pd = new ProgressDrawable(getThemePrimaryColor());
            pd.setMax(titleViewHeight + toolbarHeight);
            pd.setProgress(toolbarHeight);
            titleBackground.setBackground(pd);
        }
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        final int scrollY = scrollView.getScrollY();

        final int bodyViewY = getLocationYonScreen(bodyLayout);
        final int toolbarY = getLocationYonScreen(toolbar);

        // title has not yet collided with toolbar
        if (bodyViewY <= (toolbarHeight + titleViewHeight)) {
            final int titleTop = scrollY + toolbarHeight;
            //titleView.setTranslationY(titleTop);
            titleLayout.setTranslationY(scrollY);
            joinButton.setTranslationY(titleTop + titleViewHeight - joinButtonCenterHeight);

            if (!titleInUpperPosition && deltaY > 0) {
                titleInUpperPosition = true;

                final ObjectAnimator animPd = ObjectAnimator.ofFloat(pd, "progress", pd.max - titleViewHeight, 0f);
                animPd.setInterpolator(new DecelerateInterpolator(2f));
                animPd.setDuration(200).start();
            }

        }

        final int titleY = getLocationYonScreen(titleView);
        if (titleInUpperPosition && titleY > (toolbarHeight + toolbarY) && deltaY < 0) {
            titleInUpperPosition = false;

            final ObjectAnimator animPd = ObjectAnimator.ofFloat(pd, "progress", 0f, pd.max - titleViewHeight);
            animPd.setInterpolator(new AccelerateDecelerateInterpolator());
            animPd.setDuration(250).start();
        }

        /*if (bodyViewY >= imageViewHeight) {
            titleView.setTranslationY(imageView.getBottom() - titleViewHeight);
            joinButton.setTranslationY(imageView.getBottom() - joinButtonCenterHeight);
        }*/

        imageView.setTranslationY(scrollY * 0.5f);
    }

    private int getLocationYonScreen(View view) {
        view.getLocationOnScreen(location);
        return location[1] - statusBarHeight;
    }

    private int getActionBarHeight() {
        final TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }

    private int getStatusBarHeight() {
        final int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private int getThemePrimaryColor() {
        final TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int[] attribute = new int[] { R.attr.colorPrimary };
        final TypedArray array = obtainStyledAttributes(typedValue.resourceId, attribute);
        return array.getColor(0, Color.MAGENTA);
    }
}

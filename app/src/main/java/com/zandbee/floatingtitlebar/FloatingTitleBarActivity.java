package com.zandbee.floatingtitlebar;

/*
 * Copyright 2014 Zandbee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import android.animation.ObjectAnimator;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.PaletteAsyncListener;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zandbee.floatingtitlebar.ObservableScrollView.Callbacks;

public class FloatingTitleBarActivity extends ActionBarActivity implements Callbacks {

    // the container for everything except for a toolbar
    protected ObservableScrollView scrollView;
    protected ImageView imageView;
    // title layout: can contain subtitles, etc.
    protected FrameLayout titleLayout;
    // title background color
    protected View titleBackground;
    // title text
    protected TextView titleView;
    // content layout: place the views you need into it
    protected LinearLayout bodyLayout;
    protected TextView bodyTextView;
    protected ImageButton quasiFab;
    protected Toolbar toolbar;

    private boolean titleInUpperPosition = false;
    protected ProgressDrawable titleBackDrawable;
    private int titleBackColor;
    private int statusBarHeight;
    private int titleViewHeight;
    private int toolbarHeight;
    private int quasiFabCenterHeight;
    private int[] location = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        final View root = getLayoutInflater().inflate(R.layout.activity_floating_title_bar, null);
        setContentView(root);

        scrollView = (ObservableScrollView) root.findViewById(R.id.floating_scroll);
        scrollView.addCallbacks(this);

        imageView = (ImageView) root.findViewById(R.id.floating_img);

        bodyLayout = (LinearLayout) root.findViewById(R.id.body_layout);
        bodyTextView = (TextView) root.findViewById(R.id.test_text);

        quasiFab = (ImageButton) root.findViewById(R.id.join_group_button);
        quasiFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "I do nothing", Toast.LENGTH_SHORT).show();
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleLayout = (FrameLayout) findViewById(R.id.title_layout);
        titleView = (TextView) findViewById(R.id.floating_title_bar_text);
        titleBackground = findViewById(R.id.title_background_view);

        statusBarHeight = getStatusBarHeight();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            titleViewHeight = titleView.getHeight();
            toolbarHeight = toolbar.getHeight();
            quasiFabCenterHeight = quasiFab.getHeight() / 2;
            titleLayout.setTranslationY(imageView.getBottom() - titleViewHeight - toolbarHeight);
            quasiFab.setTranslationY(imageView.getBottom() - quasiFabCenterHeight);

            final LayoutParams layoutParams = titleBackground.getLayoutParams();
            layoutParams.height = titleViewHeight + toolbarHeight;
            titleBackground.setLayoutParams(layoutParams);

            Palette.generateAsync(drawableToBitmap(imageView.getDrawable()), new PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    titleBackColor = palette.getVibrantColor(0);
                    if (titleBackColor != 0) {
                        titleBackDrawable = new ProgressDrawable(titleBackColor);
                    } else {
                        titleBackDrawable = new ProgressDrawable(getThemePrimaryColor());
                    }

                    titleBackDrawable.setMax(titleViewHeight + toolbarHeight);
                    titleBackDrawable.setProgress(toolbarHeight);
                    titleBackground.setBackground(titleBackDrawable);
                }
            });
        }
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        final int scrollY = scrollView.getScrollY();

        /** title is going up and has not yet collided with a toolbar */
        if (getLocationYonScreen(bodyLayout) <= (toolbarHeight + titleViewHeight)) {
            titleLayout.setTranslationY(scrollY);
            quasiFab.setTranslationY(scrollY + toolbarHeight + titleViewHeight - quasiFabCenterHeight);

            // TODO try without delta
            if (!titleInUpperPosition
                    //&& deltaY > 0
                    ) {
                titleInUpperPosition = true;
                final ObjectAnimator animPd = ObjectAnimator.ofFloat(titleBackDrawable, "progress", titleBackDrawable.max - titleViewHeight, 0f);
                animPd.setInterpolator(new DecelerateInterpolator(2f));
                animPd.setDuration(200).start();
            }
        }

        /** title is going down */
        if (titleInUpperPosition && getLocationYonScreen(titleView) > toolbarHeight
                // + getLocationYonScreen(toolbar)
                && deltaY < 0) {
            titleInUpperPosition = false;
            final ObjectAnimator animPd = ObjectAnimator.ofFloat(titleBackDrawable, "progress", 0f, titleBackDrawable.max - titleViewHeight);
            animPd.setInterpolator(new AccelerateDecelerateInterpolator());
            animPd.setDuration(250).start();
        }

        imageView.setTranslationY(scrollY * 0.5f);
    }

    /** @return the view's location taking the status bar into account */
    private int getLocationYonScreen(View view) {
        view.getLocationOnScreen(location);
        return location[1] - statusBarHeight;
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

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}

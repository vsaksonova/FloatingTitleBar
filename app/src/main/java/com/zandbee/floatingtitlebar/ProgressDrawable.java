package com.zandbee.floatingtitlebar;

/*
 * Copyright 2013 Andrew Neal
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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

/**
 * A custom {@link Drawable} that can easily be used as a progress bar.
 *
 * @author Andrew Neal (andrew@seeingpixels.org)
 */
public class ProgressDrawable extends ColorDrawable {

    /**
     * The drawable's bounds Rect
     */
    private final Rect mBounds = getBounds();
    /**
     * Used to draw the progress bar
     */
    private final Paint mProgressPaint = new Paint();

    /**
     * The upper range of the progress bar
     */
    public int max = 100;

    /**
     * The current progress
     */
    public float progress;

    /**
     * Constructor for <code>ProgressDrawable</code>
     *
     * @param color The color to use
     */
    public ProgressDrawable(int color) {
        mProgressPaint.setColor(color);
        mProgressPaint.setAntiAlias(true);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(Canvas canvas) {
        final float top = mBounds.bottom * progress / max;
        canvas.drawRect(mBounds.left, top, mBounds.right, mBounds.bottom, mProgressPaint);
    }

    /**
     * Set the range of the progress bar, 0 - max
     *
     * @param max The upper range of this progress bar
     */
    public void setMax(int max) {
        this.max = max;
        invalidateSelf();
    }

    /**
     * Set the current progress to the specified value
     *
     * @param progress The new progress, between 0 and {@link #max}
     */
    public void setProgress(float progress) {
        this.progress = progress;
        invalidateSelf();
    }

}

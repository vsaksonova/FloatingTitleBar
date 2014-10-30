package com.zandbee.vstrokova.floatingtitlebartest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by Veronika Strokova on 27.10.2014.
 */
public class ObservableScrollView extends ScrollView {
    private ArrayList<Callbacks> mCallbacks = new ArrayList<Callbacks>();

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        for (Callbacks c : mCallbacks) {
            c.onScrollChanged(l - oldl, t - oldt);
        }
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    public void addCallbacks(Callbacks listener) {
        if (!mCallbacks.contains(listener)) {
            mCallbacks.add(listener);
        }
    }

    public static interface Callbacks {
        public void onScrollChanged(int deltaX, int deltaY);
    }
}

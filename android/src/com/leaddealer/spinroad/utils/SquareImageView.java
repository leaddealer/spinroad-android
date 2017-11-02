package com.leaddealer.spinroad.utils;

import android.content.Context;
import android.util.AttributeSet;

public class SquareImageView extends android.support.v7.widget.AppCompatImageView {

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int ignoredHeightMeasureSpec) {
        int newHeightMeasureSpec = widthMeasureSpec;
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
    }
}

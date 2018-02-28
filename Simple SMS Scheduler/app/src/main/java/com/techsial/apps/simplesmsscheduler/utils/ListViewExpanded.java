package com.techsial.apps.simplesmsscheduler.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ListViewExpanded extends ListView {
    public ListViewExpanded(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDividerHeight(1);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST));
    }
}
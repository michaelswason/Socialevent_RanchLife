package com.readinsite.ranchlife.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @Created by Dima on 6/10/17.
 */

public class CustomListView extends ListView {
    public CustomListView(Context context) {
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }
}

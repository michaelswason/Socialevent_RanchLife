package com.readinsite.ranchlife.ui.CustomFonts;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.readinsite.ranchlife.utils.CommonUtils;


/**
 * @author Dima 25.05.17
 */
public abstract class TextView extends AppCompatTextView {

    public TextView(final Context context) {
        super(context);
        initTypeface();
    }

    public TextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initTypeface();
    }

    public TextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypeface();
    }

    abstract protected String getTypefaceName();

    private void initTypeface() {
        CommonUtils.customTypeface(this, getTypefaceName());
    }
}

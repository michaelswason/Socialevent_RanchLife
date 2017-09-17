package com.readinsite.ranchlife.ui.CustomFonts;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.readinsite.ranchlife.utils.CommonUtils;

/**
 * @author Dima 25.05.17
 */
public abstract class EditText extends AppCompatEditText {

    public EditText(final Context context) {
        super(context);
        resetTypeface();
    }

    public EditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        resetTypeface();
    }

    public EditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resetTypeface();
    }

    public void resetTypeface() {
        CommonUtils.customTypeface(this, getTypefaceName());
    }

    abstract protected String getTypefaceName();
}

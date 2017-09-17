package com.readinsite.ranchlife.ui.CustomFonts;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author Dima 31.05.17
 */
public class TextViewBold extends TextView {

    public TextViewBold(final Context context) {
        super(context);
    }

    public TextViewBold(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewBold(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected String getTypefaceName() {
        return "fonts/raleway-semibold.ttf";
    }
}

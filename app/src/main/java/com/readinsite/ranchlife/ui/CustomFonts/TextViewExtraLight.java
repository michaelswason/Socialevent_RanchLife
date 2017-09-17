package com.readinsite.ranchlife.ui.CustomFonts;

import android.content.Context;
import android.util.AttributeSet;


/**
 * @author Dima 31.05.17
 */
public class TextViewExtraLight extends TextView {

    public TextViewExtraLight(final Context context) {
        super(context);
    }

    public TextViewExtraLight(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewExtraLight(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected String getTypefaceName() {
        return "fonts/raleway-thin.ttf";
    }
}

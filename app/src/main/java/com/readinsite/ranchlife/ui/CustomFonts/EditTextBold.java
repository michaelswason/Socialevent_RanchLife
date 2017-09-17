package com.readinsite.ranchlife.ui.CustomFonts;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author Dima 31.05.17
 */
public class EditTextBold extends EditText {

    public EditTextBold(final Context context) {
        super(context);
    }

    public EditTextBold(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextBold(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected String getTypefaceName() {
        return "fonts/raleway-semibold.ttf";
    }
}

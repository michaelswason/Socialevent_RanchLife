package com.readinsite.ranchlife.ui.CustomFonts;

import android.content.Context;
import android.util.AttributeSet;


/**
 * @author Dima 31.05.17
 */
public class EditTextRegular extends EditText {

    public EditTextRegular(final Context context) {
        super(context);
    }

    public EditTextRegular(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextRegular(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected String getTypefaceName() {
        return "fonts/raleway-regular.ttf";
    }
}

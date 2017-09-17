package com.readinsite.ranchlife.ui.CustomFonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @Created by Dima on 5/25/17.
 */

@SuppressLint("AppCompatCustomView")
public class TextViewLight extends TextView {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public TextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context, attrs);
    }

    public TextViewLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);

        Typeface customFont = selectTypeface(context, textStyle);
        setTypeface(customFont);
    }

    private Typeface selectTypeface(Context context, int textStyle) {

        switch (textStyle) {
            case Typeface.BOLD: // bold
                return Typeface.createFromAsset(context.getAssets(), "fonts/raleway-semibold.ttf");

            case Typeface.ITALIC: // light
                return Typeface.createFromAsset(context.getAssets(), "fonts/raleway-light.ttf");

            case Typeface.BOLD_ITALIC: // thin
                return Typeface.createFromAsset(context.getAssets(), "fonts/raleway-thin.ttf");

            case Typeface.NORMAL: // regular
            default:
                return Typeface.createFromAsset(context.getAssets(), "fonts/raleway-light.ttf");
        }
    }
}

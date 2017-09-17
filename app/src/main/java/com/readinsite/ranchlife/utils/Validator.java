package com.readinsite.ranchlife.utils;

import android.text.TextUtils;

import java.util.List;


public class Validator {

    public static boolean isEmpty(List list) {
        return null == list || list.isEmpty();
    }

    /**
     * @throws NullPointerException if an object is null
     */
    public static void notEmptyOrThrow(CharSequence... charSequences) {
        for (int i = 0; i < charSequences.length; i++) {
            if ( TextUtils.isEmpty(charSequences[i]) ) {
                throw new NullPointerException(i + " object is null.");
            }
        }
    }

    public static boolean notNull(Object... objects) {
        try {
            notNullOrThrow(objects);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * @throws NullPointerException if an object is null
     */
    public static void notNullOrThrow(Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            if ( null == objects[i] ) {
                throw new NullPointerException(i + " object is null.");
            }
        }
    }
}

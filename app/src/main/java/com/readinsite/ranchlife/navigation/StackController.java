package com.readinsite.ranchlife.navigation;

import android.support.v4.app.Fragment;

/**
 * @author mbelsky 27.01.16
 */
public interface StackController {
    void pushFragment(Fragment fragment);
    void popFragment();
    void popToRootFragment();
}

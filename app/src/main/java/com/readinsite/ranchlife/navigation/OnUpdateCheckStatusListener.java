package com.readinsite.ranchlife.navigation;

/**
 * @author mbelsky 28.01.16
 *
 * An interface to get updates when a user successefully check in or check out
 */
public interface OnUpdateCheckStatusListener {
    /**
     *
     * @param isChecked true if a crew is checked in, false if she is checked out
     */
    void onUpdateCheckStatus(boolean isChecked);
}

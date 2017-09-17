package com.readinsite.ranchlife.utils;

import android.support.annotation.MenuRes;

import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.fragment.BaseFragment;
import com.readinsite.ranchlife.fragment.CommunityCalendarFragment;
import com.readinsite.ranchlife.fragment.HomeFragment;
import com.readinsite.ranchlife.fragment.MyFeedFragment;
import com.readinsite.ranchlife.fragment.PAFragment;


public class RootFragmentFactory {
    public static BaseFragment getFragment(@MenuRes int menuItemId) {
        switch ( menuItemId ) {
            case R.id.main_navigation_item_home:
                return HomeFragment.newInstance();
            case R.id.main_navigation_item_feed:
                return MyFeedFragment.newInstance();
            case R.id.main_navigation_item_community:
                return CommunityCalendarFragment.newInstance();
            case R.id.main_navigation_item_pa:
                return PAFragment.newInstance();
            case R.id.main_navigation_item_following:
                return CommunityCalendarFragment.newInstance();
            case R.id.main_navigation_item_settings:
                return CommunityCalendarFragment.newInstance();
            case R.id.main_navigation_item_user_management:
                return CommunityCalendarFragment.newInstance();
            case R.id.main_navigation_item_role_management:
                return CommunityCalendarFragment.newInstance();
            case R.id.main_navigation_item_trigger_management:
                return CommunityCalendarFragment.newInstance();
            case R.id.main_navigation_item_event_management:
                return CommunityCalendarFragment.newInstance();
            case R.id.main_navigation_item_pa_management:
                return CommunityCalendarFragment.newInstance();
            default:
                throw new IllegalArgumentException();

        }
    }
}

package com.readinsite.ranchlife.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.readinsite.ranchlife.fragment.EventDetailsFragment;

/**
 * @author mbelsky 07.09.15
 */
public class EventDetailsActivity extends FragmentActivity {
    public static final String EVENT_ID = "com.readinsite.ranchlife.activity.EventDetailsActivity.event_id";
    public boolean editablePackage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if ( null == savedInstanceState ) {

            //    replaceFragment(EventDetailsFragment.newInstance(event));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( android.R.id.home == item.getItemId() ) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

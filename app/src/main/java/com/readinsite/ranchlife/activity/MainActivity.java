package com.readinsite.ranchlife.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.app.UserPreferences;
import com.readinsite.ranchlife.dialog.NoNetworkFragmentDialog;
import com.readinsite.ranchlife.fragment.CommunityCalendarFragment;
import com.readinsite.ranchlife.navigation.OnUpdateCheckStatusListener;
import com.readinsite.ranchlife.navigation.StackController;
import com.readinsite.ranchlife.ui.NavigationDrawerHeaderHolder;
import com.readinsite.ranchlife.utils.CommonUtils;
import com.readinsite.ranchlife.utils.Intents;
import com.readinsite.ranchlife.utils.RootFragmentFactory;
import com.readinsite.ranchlife.service.TrackerService;

import android.support.design.widget.NavigationView;
import android.widget.FrameLayout;

public class MainActivity extends FragmentActivity implements NoNetworkFragmentDialog.Callback, NavigationView.OnNavigationItemSelectedListener, StackController, OnUpdateCheckStatusListener, View.OnClickListener {
    public static final String ACTION_SHOW_NAVIGATION_MENU = "com.readinsite.ranchlife.activity.MainActivity.SHOW_NAVIGATION_MENU";
    private static final String BUNDLE_NEED_RELOAD = "com.readinsite.ranchlife.activity.MainActivity.NEED_RELOAD";
    private static final String BUNDLE_SELECTED_NAVIGATION_ITEM = "com.readinsite.ranchlife.activity.MainActivity.SELECTED_NAVIGATION_ITEM";

    private DialogFragment noInternetConnectionDialog;
    private UserPreferences prefrences = UserPreferences.getInstance();

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private NavigationDrawerHeaderHolder headerHolder;
    private static final int LOCATION_PERMISSION_ID = 1001;
    private final static int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 1;

    protected boolean needReloadStatus = true;
    private volatile boolean resumed;

    Messenger mService = null;
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private final Handler handler = new Handler();
    private @IdRes
    int selectedNavigationItem = -1;


    private final FragmentManager.OnBackStackChangedListener onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            boolean drawerIndicatorEnabled =
                    0 == getSupportFragmentManager().getBackStackEntryCount();

            drawerToggle.setDrawerIndicatorEnabled(drawerIndicatorEnabled);

            drawerLayout.setDrawerLockMode(drawerIndicatorEnabled
                    ? DrawerLayout.LOCK_MODE_UNLOCKED
                    : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    };

    private final DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        private boolean hasUpdated;

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            drawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            drawerToggle.onDrawerOpened(drawerView);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            drawerToggle.onDrawerClosed(drawerView);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            drawerToggle.onDrawerStateChanged(newState);

            if ( !hasUpdated && DrawerLayout.STATE_DRAGGING == newState ) {
                updateNavigationHeaderTitles();
            }
            hasUpdated = DrawerLayout.STATE_DRAGGING == newState;
        }
    };

    private LocalBroadcastManager broadcastManager;
    private final BroadcastReceiver showNavigationMenuBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            drawerLayout.openDrawer(GravityCompat.START);
            updateNavigationHeaderTitles();
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");

        broadcastManager = LocalBroadcastManager.getInstance(this);

        if ( null != savedInstanceState ) {
            needReloadStatus = savedInstanceState.getBoolean(BUNDLE_NEED_RELOAD, true);
            selectedNavigationItem = savedInstanceState.getInt(BUNDLE_SELECTED_NAVIGATION_ITEM, -1);
        }
//        //download profile image
//        try {
//            CommonUtils.deleteFile(MainActivity.this, PROFILE_PATH);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        boolean isStaff = preferences.isStaffUser();
//
//        String IMAGE_URL;
//        if (preferences.isClientUser()) {
//            IMAGE_URL = Constants.CLIENT_PROFILE_IMAGE_INTEGRA_PATH + preferences.getName() + PROFILE_PATH;
//        } else {
//            IMAGE_URL = isStaff ? STAFF_PROFILE_IMAGE_INTEGRA_PATH + preferences.getEmail() + ".jpg" : CREW_PROFILE_IMAGE_INTEGRA_PATH + preferences.getName() + PROFILE_PATH;
//        }
//
//        new LoadImageTask(this).execute(IMAGE_URL);


        if(Build.VERSION.SDK_INT >= 23)
            checkPermissions();

    }

    @SuppressLint("InlinedApi")
    private boolean checkPermissions() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (TrackerService.isRunning()) {
                doUnbindService();
                stopService(new Intent(MainActivity.this,
                        TrackerService.class));
            }
            startService(new Intent(MainActivity.this, TrackerService.class));
            doBindService();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
        }

        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_fragment_toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setVisibility(View.GONE);

        drawerLayout = (DrawerLayout)findViewById(R.id.activity_main_drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.activity_main_view_navigation);
        //noinspection ConstantConditions
        navigationView.setNavigationItemSelectedListener(this);
        FrameLayout headerNav = (FrameLayout) findViewById(R.id.activity_main_view_header);
        headerHolder = new NavigationDrawerHeaderHolder(headerNav);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerToggle.setHomeAsUpIndicator(R.drawable.ic_action_navigation_arrow_back);

        findViewById(R.id.activity_main_view_btn_logout).setOnClickListener(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
        // Call to restore drawer indicator
        onBackStackChangedListener.onBackStackChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setResumed(true);

        broadcastManager.registerReceiver(showNavigationMenuBroadcastReceiver,
                new IntentFilter(ACTION_SHOW_NAVIGATION_MENU));
        getSupportFragmentManager().addOnBackStackChangedListener(onBackStackChangedListener);
        drawerLayout.addDrawerListener(drawerListener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();


        prepareNavigationMenu();

    }

    @Override
    protected void onPause() {
        super.onPause();
        setResumed(false);

        broadcastManager.unregisterReceiver(showNavigationMenuBroadcastReceiver);
        getSupportFragmentManager().removeOnBackStackChangedListener(onBackStackChangedListener);
        drawerLayout.removeDrawerListener(drawerListener);

        if ( null != noInternetConnectionDialog ) {
            noInternetConnectionDialog.dismiss();
            noInternetConnectionDialog = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_NEED_RELOAD, needReloadStatus);
        outState.putInt(BUNDLE_SELECTED_NAVIGATION_ITEM, selectedNavigationItem);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if ( android.R.id.home == item.getItemId() ) {
                if ( !drawerToggle.onOptionsItemSelected(item) ) {
                    // Drawer Indicator Disabled
                    popToRootFragment();
                }

                return true;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if ( drawerLayout.isDrawerOpen(GravityCompat.START) ) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();

        }
    }

    private void navigationItemToday(@NonNull final MenuItem item) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ( !isResumed() ) {
                    return;
                }
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.main_navigation_item_community) {
                    drawerLayout.closeDrawer(GravityCompat.START);

                    selectedNavigationItem = -1;

                    replaceFragment(CommunityCalendarFragment.newInstance(true), false, false);

                    Toolbar toolbar = (Toolbar) findViewById(R.id.activity_fragment_toolbar);
                    showLogoTitle(false);

                    toolbar.setVisibility(View.VISIBLE);
                }

            }
        }, 250);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ( !isResumed() ) {
                    return;
                }

                drawerLayout.closeDrawer(GravityCompat.START);

                int menuItemId = item.getItemId();

                if ( selectedNavigationItem != menuItemId ) {
                    selectedNavigationItem = menuItemId;

                    if (menuItemId == R.id.main_navigation_item_feed) {
                        replaceFragment(RootFragmentFactory.getFragment(menuItemId), false, true);
                    } else {
                        replaceFragment(RootFragmentFactory.getFragment(menuItemId), false, false);
                    }

                    Toolbar toolbar = (Toolbar)findViewById(R.id.activity_fragment_toolbar);

                    if (menuItemId == R.id.main_navigation_item_home) {
                        toolbar.setVisibility(View.GONE);
                    } else if (menuItemId == R.id.main_navigation_item_feed) {
                        toolbar.setVisibility(View.VISIBLE);
                        showLogoTitle(true);
                    } else {
                        showLogoTitle(false);
                        toolbar.setVisibility(View.VISIBLE);

                    }

                }
            }
        }, 250);

        return true;
    }

    @Override
    public void onOkClick() {
    }


    private void prepareNavigationMenu() {
        needReloadStatus = false;

        updateNavigationHeaderTitles();

        final int firstItemTitle;
            firstItemTitle = R.string.home;

        if (!prefrences.isAdminUser()) {
            navigationView.getMenu().removeItem(R.id.main_navigation_item_user_management);
            navigationView.getMenu().removeItem(R.id.main_navigation_item_role_management);
            navigationView.getMenu().removeItem(R.id.main_navigation_item_trigger_management);
            navigationView.getMenu().removeItem(R.id.main_navigation_item_event_management);
            navigationView.getMenu().removeItem(R.id.main_navigation_item_pa_management);
        }

        MenuItem checkItem = navigationView.getMenu().getItem(0);
        checkItem.setTitle(firstItemTitle);


        MenuItem selectedMenuItem = -1 == selectedNavigationItem
                ? checkItem
                : navigationView.getMenu().findItem(selectedNavigationItem);

        navigationView.setCheckedItem(selectedMenuItem.getItemId());
        onNavigationItemSelected(selectedMenuItem);
    }

    public void navigateMenu(int menuId, boolean isToday) {
        MenuItem item =  navigationView.getMenu().findItem(menuId);
        if (menuId == R.id.main_navigation_item_community && isToday) {
            navigationItemToday(item);
        } else {
            onNavigationItemSelected(item);
        }
    }

    private void updateNavigationHeaderTitles() {
        headerHolder.showAdmin(prefrences.isAdminUser());
        headerHolder.setTitles(prefrences.getName(), prefrences.getSurname());
        headerHolder.setImage(MainActivity.this, prefrences.getProfilePicture());
    }

    private synchronized boolean isResumed() {
        return resumed;
    }

    private synchronized void setResumed(final boolean resumed) {
        this.resumed = resumed;
    }

    @Override
    public void pushFragment(Fragment fragment) {
        replaceFragment(fragment, true);
    }

    @Override
    public void popFragment() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void popToRootFragment() {
        getSupportFragmentManager().popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onUpdateCheckStatus(boolean isChecked) {
        if ( isResumed() ) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    selectedNavigationItem = -1;

                    popToRootFragment();
                    prepareNavigationMenu();
                }
            });
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (TrackerService.isRunning()) {
                doUnbindService();
                stopService(new Intent(MainActivity.this,
                        TrackerService.class));
            }
            startService(new Intent(MainActivity.this, TrackerService.class));
            doBindService();
        }

        Intent result = Intents.buildShareRequestPermissionsResult(requestCode, permissions,
                grantResults);
        LocalBroadcastManager.getInstance(this).sendBroadcast(result);

    }

    @Override
    public void onClick(View v) {

        switch ( v.getId() ) {
            case R.id.activity_main_view_btn_logout:
                if (TrackerService.isRunning()) {
                    doUnbindService();
                    stopService(new Intent(MainActivity.this,
                            TrackerService.class));
                }

                CommonUtils.showLoginScreen(MainActivity.this);
                break;
            default:
                throw new RuntimeException("Unhandled action");
        }
    }

    @SuppressLint("HandlerLeak")
    private class IncomingHandler extends Handler {
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null,
                        TrackerService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }
            catch (RemoteException e) {
                Log.e("osama", "error connecting to service: " + e.getMessage());
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            Log.e("osama", "disconnected from service :(");
        }
    };

    public void doBindService() {
        bindService(new Intent(this, TrackerService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    public void doUnbindService() {
        if (!mIsBound)
            return;

        if (mService != null) {
            try {
                Message msg = Message.obtain(null,
                        TrackerService.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        unbindService(mConnection);
        mIsBound = false;
    }


}

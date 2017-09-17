package com.readinsite.ranchlife.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.readinsite.ranchlife.R;

public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView();
    }

    protected void initContentView() {
        setContentView(R.layout.activity_fragment);
        setSupportActionBar((Toolbar)findViewById(R.id.activity_fragment_toolbar));
    }

    protected final void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, false);
    }

    protected final void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(fragment, addToBackStack, false);
    }

    public static void setOverflowButtonColor(final Toolbar toolbar, final int color) {
        Drawable drawable = toolbar.getOverflowIcon();
        if(drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), color);
            toolbar.setOverflowIcon(drawable);
        }
    }

    protected final void replaceFragment(Fragment fragment, boolean addToBackStack, boolean isFullMode) {
        @SuppressLint("CommitTransaction")
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(getFullContainerId() , fragment, fragment.getClass().getName());
        if ( addToBackStack ) {
            transaction.addToBackStack(null);
        }
        transaction.commit();


        int color = isFullMode ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
        final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_fragment_toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            final View v = toolbar.getChildAt(i);

            if (v instanceof ImageButton) {
                ((ImageButton) v).setColorFilter(colorFilter);
            }
        }

    }

    protected @IdRes
    int getFullContainerId() {
        return R.id.activity_fragment_full_container;
    }

    public void showLogoTitle(boolean showed) {
        LinearLayout view = (LinearLayout)findViewById(R.id.activity_fragment_toolbar_logo);
        view.setVisibility(showed ? View.VISIBLE : View.GONE);

        int color = showed ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
        final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_fragment_toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            final View v = toolbar.getChildAt(i);

            if (v instanceof ImageButton) {
                ((ImageButton) v).setColorFilter(colorFilter);
            }
        }
    }

    public void showAlertDialog(Activity activity, String title, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void onNetworkFailed(Activity activity){
        Toast.makeText(activity, R.string.check_network_connection, Toast.LENGTH_LONG).show();
    }

    public void onConnectionFailed(Activity activity){
        Toast.makeText(activity, R.string.connection_failed, Toast.LENGTH_LONG).show();
    }

    public void startIndicator() {
        findViewById(R.id.view_indicator).setVisibility(View.VISIBLE);
        findViewById(R.id.view_indicator).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    public void stopIndicator() {
        findViewById(R.id.view_indicator).setVisibility(View.GONE);
    }

}

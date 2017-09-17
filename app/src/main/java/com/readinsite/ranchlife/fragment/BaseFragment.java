package com.readinsite.ranchlife.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.dialog.NoNetworkFragmentDialog;
import com.readinsite.ranchlife.navigation.StackController;

import java.io.IOException;


public class BaseFragment extends Fragment implements NoNetworkFragmentDialog.Callback {

    private StackController stackController;

    private DialogFragment loadingDialog;
    private DialogFragment noNetworkDialog;
    private Handler handler;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if ( getActivity() instanceof StackController ) {
            stackController = (StackController)getActivity();
        }
        handler = new Handler();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ( null != savedInstanceState ) {
            onRestoreInstanceState(savedInstanceState);
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissLoadingDialog();
        dismissNoNetworkDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        setSubtitle(null);
    }

    @Override
    public void onOkClick() {
    }
//
//    @Override
//    public void onFailure(final Request request, final IOException e) {
//        if ( isResumed() ) {
//            dismissLoadingDialog();
//            showNoNetworkDialog();
//        }
//    }
//
//    @Override
//    public void onResponse(final Response response) throws IOException {
//        dismissLoadingDialog();
//    }

    protected void dismissLoadingDialog() {
        if ( null != loadingDialog ) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    protected void dismissNoNetworkDialog() {
        if ( null != noNetworkDialog ) {
            noNetworkDialog.dismiss();
            noNetworkDialog = null;
        }
    }

    protected Handler getHandler() {
        return handler;
    }

    protected void post(final Runnable runnable) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if ( isResumed() ) {
                    runnable.run();
                }
            }
        });
    }

    public void setTitle(@StringRes int title) {
        setTitle(getString(title));
    }

    public void setTitle(CharSequence title) {
        //noinspection ConstantConditions
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
    }

    public void setSubtitle(CharSequence subtitle) {
        //noinspection ConstantConditions
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(subtitle);
    }

    protected final void showLoadingDialog() {
//        loadingDialog = LoadingDialogFragment.newInstance();
//        loadingDialog.show(getFragmentManager(), LoadingDialogFragment.class.getName());
    }

    protected final void showNoNetworkDialog() {
        noNetworkDialog = NoNetworkFragmentDialog.newInstance(this);
        noNetworkDialog.show(getFragmentManager(),
                             NoNetworkFragmentDialog.class.getName());
    }

    protected void toastCheckInternetConnection() {
        if ( !isResumed() ) {
            return;
        }

        post(new Runnable() {
            @Override
            public void run() {
                Context context = getActivity();
                if ( null != context ) {
                    Toast.makeText(context, R.string.check_internet_connection,
                                   Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected final void pushFragment(Fragment fragment) {
        if ( null != stackController ) {
            stackController.pushFragment(fragment);
        }
    }

    protected final void popFragment() {
        if ( null != stackController ) {
            stackController.popFragment();
        }
    }

    protected final void popToRootFragment() {
        if ( null != stackController ) {
            stackController.popToRootFragment();
        }
    }
}

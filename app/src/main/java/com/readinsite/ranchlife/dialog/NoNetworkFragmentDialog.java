package com.readinsite.ranchlife.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.readinsite.ranchlife.R;


public class NoNetworkFragmentDialog extends AppCompatDialogFragment implements DialogInterface.OnClickListener {

    public interface Callback {
        void onOkClick();
    }

    public static NoNetworkFragmentDialog newInstance(Callback callback) {
        NoNetworkFragmentDialog fragment = new NoNetworkFragmentDialog();
        fragment.callback = callback;
        return fragment;
    }

    private Callback callback;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.no_internet_connection)
                .setMessage(R.string.check_internet_connection)
                .setPositiveButton("TRY AGAIN", this)
                .setNegativeButton(android.R.string.cancel, this)
                .create();
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        switch ( which ) {
            case AlertDialog.BUTTON_POSITIVE:
                if ( null != callback ) {
                    callback.onOkClick();
                }
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                getActivity().finish();
                break;
        }
    }
}
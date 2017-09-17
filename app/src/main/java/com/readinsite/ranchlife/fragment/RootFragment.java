package com.readinsite.ranchlife.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readinsite.ranchlife.R;


public abstract class RootFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( displayFragmentView() ) {
            return inflater.inflate(getFragmentViewResId(), container, false);
        }

        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_root_no_content, container,
                                                     false);
        ViewGroup fragmentContainer = (ViewGroup)view
                .findViewById(R.id.fragment_root_no_content_view);

        // Inflate to have no NPE exceptions
        inflater.inflate(getFragmentViewResId(), fragmentContainer, true);

        if ( !TextUtils.isEmpty(getNoContentViewCustomMessage()) ) {
            TextView tvMessage =
                    (TextView)view.findViewById(R.id.fragment_root_no_content_tv_message);
            tvMessage.setText(getNoContentViewCustomMessage());
        }

        return view;
    }

    protected String getNoContentViewCustomMessage() {
        int resId = R.string.app_title;
        return getString(resId);
    }

    protected boolean displayFragmentView() {
        return true;
    }

    protected abstract int getFragmentViewResId();
}

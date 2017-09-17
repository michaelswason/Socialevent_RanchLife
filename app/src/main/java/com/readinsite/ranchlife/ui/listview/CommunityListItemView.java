package com.readinsite.ranchlife.ui.listview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.readinsite.ranchlife.R;


/**
 * @author Dima 09/06/17
 */
public class CommunityListItemView extends LinearLayoutCompat implements Checkable {

    private boolean isChecked;

    public CommunityListItemView(final Context context) {
        super(context);
        initView();
    }

    public CommunityListItemView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public void setChecked(final boolean checked) {
        isChecked = checked;

        int[] state;
        if ( isChecked() ) {
            state = new int[] {android.R.attr.state_checked};
        } else {
            state = null;
        }

        Drawable background = getBackground();
        if ( null != background ) {
            background.setState(state);
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    private void initView() {
        setOrientation(HORIZONTAL);
        inflate(getContext(), R.layout.view_community_item, this);
    }
}

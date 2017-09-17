package com.readinsite.ranchlife.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.rest.ApiClient;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


/**
 * @author mbelsky 28.01.16
 */
public class NavigationDrawerHeaderHolder {

    private android.widget.TextView tvTitle;
    private android.widget.TextView tvSubtitle, tvAdmin;
    private android.widget.ImageView tvImageView;
    private FrameLayout viewGroup;

    public NavigationDrawerHeaderHolder(View navigationDrawerHeader) {
        if ( navigationDrawerHeader instanceof FrameLayout) {
            viewGroup = (FrameLayout)navigationDrawerHeader;
        } else {
            throw new RuntimeException("Unexpected view group class");
        }

        tvTitle = (android.widget.TextView)viewGroup
                .findViewById(R.id.view_navigation_drawer_header_tv_title);
        tvSubtitle = (android.widget.TextView)viewGroup
                .findViewById(R.id.view_navigation_drawer_header_tv_subtitle);
        tvImageView = (android.widget.ImageView) viewGroup.findViewById(R.id.view_navigation_drawer_header_tv_image);
        tvAdmin = (TextView)viewGroup.findViewById(R.id.view_navigation_drawer_header_tv_admin);


        if ( null == tvTitle || null == tvSubtitle ) {
            throw new RuntimeException("This view has no a required text view");
        }
    }

    public void setTitles(String title, String subtitle) {
        tvTitle.setText(title);
        tvSubtitle.setText(subtitle);
    }
    public void setImage(Context context, String path) {
        if (path != null && !path.equals("")) {
            Picasso.with(context).load(String.format("%s%s", ApiClient.BASE_IMAGE_URL, path)).into(tvImageView);
        }
    }
    public void showAdmin(boolean isShowed) {
        tvAdmin.setVisibility(isShowed ? View.VISIBLE : View.GONE);
    }
}

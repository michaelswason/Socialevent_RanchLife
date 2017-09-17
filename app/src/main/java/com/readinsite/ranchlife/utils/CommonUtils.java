package com.readinsite.ranchlife.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.activity.LoginActivity;
import com.readinsite.ranchlife.app.UserPreferences;
import com.readinsite.ranchlife.model.OEvent;
import com.readinsite.ranchlife.model.UserResponse;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class CommonUtils {
    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static Date parseStringToDate(String dateStr) {
        Date date = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String getDayFromDate(Date date) {
        return (String) DateFormat.format("dd",   date);
    }

    public static String getDayOfWeekFromDate(Date date) {
        return (String) DateFormat.format("EEEE", date);
    }

    public static String getMonthStringFromDate(Date date) {
        return (String) DateFormat.format("MMM",  date);
    }

    public static String getYearFromDate(Date date) {
        return (String) DateFormat.format("yyyy", date);
    }

    public static String getHourFromDate(Date date) {
        return (String) DateFormat.format("HH", date);
    }

    public static String getMinuteFromDate(Date date) {
        return (String) DateFormat.format("mm", date);
    }

    public static void customTypeface(@NonNull TextView view, @NonNull String typeface) {
        Typeface t = Typeface.createFromAsset(view.getContext().getAssets(), typeface);
        view.setTypeface(t);
    }

    public static float convertCelsiusToFahrenheit(float celsius) {
        return ((celsius * 9) / 5) + 32;
    }

    public static void hideSoftInput(View view) {
        InputMethodManager manager = (InputMethodManager)view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isPortraitMode(@NonNull Context context) {
        Validator.notNullOrThrow(context);
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean isVisible(View view) {
        return View.VISIBLE == view.getVisibility();
    }

    public static void showLoginScreen(@NonNull Activity activity) {
        Validator.notNullOrThrow(activity);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<OEvent> results = realm.where(OEvent.class).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.delete(OEvent.class);
            }
        });

        UserPreferences preferences = UserPreferences.getInstance();
        preferences.reset();
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    public static void dismissDialogFragment(FragmentManager fragmentManager, String dialogTag) {
        Fragment fragment = fragmentManager.findFragmentByTag(dialogTag);
        if ( fragment instanceof DialogFragment) {
            ((DialogFragment)fragment).dismiss();
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());

        Matrix thm_matrix = new Matrix();
        // RESIZE THE BIT MAP
        float thum_scaleWidth = ratio;
        float thum_scaleHeight = ratio;
        thm_matrix.postScale(thum_scaleWidth, thum_scaleHeight);

        return Bitmap.createBitmap(realImage, 0, 0, realImage.getWidth(), realImage.getHeight(), thm_matrix, false);
    }

    public static Bitmap getThumbnail(Bitmap realImage) {
        float maxImageSize = 80;

        return scaleDown(realImage, maxImageSize, true);
    }

    public static boolean saveOutput(Uri mSaveUri, Bitmap bitmap, Context context) {
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            ContentResolver mContentResolver = context.getContentResolver();
            Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;

            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    bitmap.compress(mOutputFormat, 90, outputStream);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } finally {
                closeSilently(outputStream);
            }
        } else {
            return false;
        }

        bitmap.recycle();
        return true;
    }

    public static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    public static void saveFile(Bitmap bitmap, File file) throws IOException {
        FileOutputStream fo;
        try{
            file.createNewFile();
            fo = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo);

            fo.flush();
            fo.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static File createFile(Context context, String filename) throws IOException {
        File result;
        if (filename == null)
            return null;

        String path = context.getExternalFilesDir(null).getAbsolutePath() + filename;
        result = new File(path);

        if (result.exists())
            result.delete();

        result = new File(path);

        return result;
    }

    public static File createFile(String path) throws IOException {
        File result;
        try {
            result = new File(path);

            if (result.exists())
                result.delete();

            result = new File(path);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public static boolean deleteFile(Context context, String filename) throws IOException {
        File result;
        String path = context.getExternalFilesDir(null).getAbsolutePath() + filename;
        result = new File(path);

        if (result.exists()) {
            result.delete();
            return true;
        }
        return false;
    }

    public static File getFile(Context context, String filename) throws IOException {
        File result;
        String path = context.getExternalFilesDir(null).getAbsolutePath() + filename;
        result = new File(path);
        if (result.exists())
            return result;

        return null;
    }

    public static boolean IfWithinStorehouse(Location userLoc, double storehouseLat, double storehouseLong, double distance){
        if (userLoc != null) {
            Location loc2 = new Location(LocationManager.GPS_PROVIDER);
            loc2.setLatitude(storehouseLat);
            loc2.setLongitude(storehouseLong);
            float distanceInMeters = userLoc.distanceTo(loc2);
            float mile = distanceInMeters / 1609.34f;

            return mile < distance;
        }
        return false;
    }

    public static void showAlert(Context context, String title, String mess) {
        AlertDialog.Builder oBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        oBuilder.setTitle(title);
        oBuilder.setMessage(mess);
        oBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        oBuilder.show();
    }

}

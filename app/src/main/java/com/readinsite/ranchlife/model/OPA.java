package com.readinsite.ranchlife.model;

import android.annotation.SuppressLint;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;


public class OPA implements Serializable {
    @SerializedName("id")
    public long paId;

    @SerializedName("title")
    public String title;

    @SerializedName("text")
    public String details;

    @SerializedName("picture")
    public String picturePath;

    @SerializedName("datetime")
    public String paDate;

    @SerializedName("email")
    public String email;

    @SerializedName("todate")
    public String todate;

    @SerializedName("fromdate")
    public String fromdate;

    @SerializedName("weekly")
    public String weekly;

    @SerializedName("location")
    public String location;

    @SerializedName("latitude")
    public String  latitude;

    @SerializedName("longitude")
    public String  longitude;

    @SerializedName("allimages")
    public String  slideImages;

    @SerializedName("videourl")
    public String  videoPath;

    public OPA(){
        paId = 0;
        title = "";
        details = "";
        picturePath = "";
        paDate = "";
        todate = "";
        fromdate = "";
        weekly = "";
        email = "";
        location = "";
        latitude = "0";
        longitude = "0";
        slideImages = "";
        videoPath = "";
    }

    public static boolean isPAforToday(OPA oPA ) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String[] weelyItems = oPA.weekly.split(",");
        for (String wk : weelyItems) {
            if (wk.equals("su") && dayOfWeek == 7)   //1
                return true;

            if (wk.equals("mo") && dayOfWeek == 1) {
                return true;
            }

            if (wk.equals("tu") && dayOfWeek == 2) {
                return true;
            }

            if (wk.equals("we") && dayOfWeek == 3) {
                return true;
            }

            if (wk.equals("th") && dayOfWeek == 4) {
                return true;
            }

            if (wk.equals("fr") && dayOfWeek == 5) {
                return true;
            }

            if (wk.equals("sa") && dayOfWeek == 6) {
                return true;
            }
        }

        return false;
    }

    public static boolean isEalierThanNow(String paTimeStr) {
        if (paTimeStr == null || paTimeStr.isEmpty()) {
            return false;
        }

        String[] items = paTimeStr.split(":");
        if (items.length == 2) {
            try {
                String hourStr = items[0].trim();
                String minStr = items[1].trim();
                int hour = Integer.parseInt(hourStr);
                int min = Integer.parseInt(minStr);

                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(new Date());

                    int curHr = calendar.get(Calendar.HOUR);
                    int curMin = calendar.get(Calendar.MINUTE);

                    if (curHr > hour) {
                        return true;
                    } else if (curHr == hour && curMin > min) {
                        return true;
                    } else {
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    @SuppressLint("DefaultLocale")
    public static String convert12HoursFormat(String timeStr) {
        String newTimeStr = "";

        try {
            String[] items = timeStr.split(":");
            if (items.length == 2) {
                String hourStr = items[0];
                String minStr = items[1];

                String am = "AM";
                int hour = Integer.parseInt(hourStr);
                if (hour >= 12) {
                    hour = hour - 12;
                    am = "PM";
                } else {
                    if (hour == 0)
                        hour = 12;
                    am = "AM";
                }

               newTimeStr = String.format("%d : %s %s", hour, minStr, am);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return newTimeStr;
    }

}

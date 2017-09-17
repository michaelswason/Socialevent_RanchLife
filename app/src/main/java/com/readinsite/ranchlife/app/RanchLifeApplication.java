package com.readinsite.ranchlife.app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.readinsite.ranchlife.activity.MainActivity;
import com.readinsite.ranchlife.model.OEvent;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * @author Dima 07/06/17
 */
public class RanchLifeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        UserPreferences.getInstance(this);
        OneSignal.startInit(this)
                .autoPromptLocation(false) // default call promptLocation later
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.e("debug", "User:" + userId);
                UserPreferences preferences = UserPreferences.getInstance();
                preferences.setDeviceId(userId);
                if (registrationId != null)
                    Log.e("debug", "registrationId:" + registrationId);

            }
        });

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
    }

    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            getNotificationData(data);

            String notificationID = notification.payload.notificationID;
            //We will use further.
            /*
            String title = notification.payload.title;
            String body = notification.payload.body;
            String smallIcon = notification.payload.smallIcon;
            String largeIcon = notification.payload.largeIcon;
            String bigPicture = notification.payload.bigPicture;
            String smallIconAccentColor = notification.payload.smallIconAccentColor;
            String sound = notification.payload.sound;
            String ledColor = notification.payload.ledColor;
            int lockScreenVisibility = notification.payload.lockScreenVisibility;
            String groupKey = notification.payload.groupKey;
            String groupMessage = notification.payload.groupMessage;
            String fromProjectNumber = notification.payload.fromProjectNumber;
            //BackgroundImageLayout backgroundImageLayout = notification.payload.backgroundImageLayout;
            String rawPayload = notification.payload.rawPayload;
            */
            String customKey;

            Log.e("OneSignalExample", "NotificationID received: " + notificationID);

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.e("OneSignalExample", "customkey set with value: " + customKey);
            }
        }
    }


    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl

            String customKey;
            String openURL = null;
            Object activityToLaunch = MainActivity.class;

            if (data != null) {
                customKey = data.optString("customkey", null);
                openURL = data.optString("openURL", null);

                if (customKey != null)
                    Log.e("OneSignalExample", "customkey set with value: " + customKey);

                if (openURL != null)
                    Log.e("OneSignalExample", "openURL to webview with URL value: " + openURL);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                Log.e("OneSignalExample", "Button pressed with id: " + result.action.actionID);


            }
            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
            // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
            Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("openURL", openURL);
            Log.i("OneSignalExample", "openURL = " + openURL);
            // startActivity(intent);
            startActivity(intent);

            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */




        }
    }

    private void getNotificationData(JSONObject data) {
        if (data == null )
            return;
        try {
            int success = data.getInt("success");
            if (success == 1) {
                JSONObject jsonData = data.getJSONObject("data");

                if (jsonData != null) {
                    String email = jsonData.isNull("email") ? "" : jsonData.getString("email");
                    String firstname = jsonData.getString("firstname");
                    String lastname = jsonData.getString("lastname");
                    JSONObject jsonInfo = jsonData.getJSONObject("eventinfo");


                    try {
                        if (jsonInfo != null) {
                            long eventId = jsonInfo.getLong("eventid");

                            Realm realm = Realm.getDefaultInstance();

                            RealmResults<OEvent> results = realm.where(OEvent.class).equalTo("eventId", eventId).equalTo("followerEmail", email).findAll();
                            realm.beginTransaction();
                            results.deleteAllFromRealm();
                            realm.commitTransaction();

                            realm.beginTransaction();
                            OEvent oEvent = realm.createObject(OEvent.class);
                            oEvent.eventId = eventId;
                            oEvent.name = jsonInfo.getString("name");
                            oEvent.title = jsonInfo.getString("title");
                            oEvent.details = jsonInfo.getString("subtext");
                            oEvent.latitude = jsonInfo.getString("latitude");
                            oEvent.longitude = jsonInfo.getString("longitude");
                            oEvent.triggerId = jsonInfo.getLong("triggerid");
                            oEvent.paId = jsonInfo.getLong("paid");
                            oEvent.iconPath = jsonInfo.getString("icon");
                            oEvent.imagePath = jsonInfo.getString("picture");
                            oEvent.slideImages = jsonInfo.getString("allimages");
                            oEvent.videourl = jsonInfo.getString("videourl");
                            oEvent.maxReservableCount = jsonInfo.getInt("maxreservenum");
                            oEvent.currentReservedCount = jsonInfo.getInt("curreservenum");
                            oEvent.eventDate = jsonInfo.getString("datetime");
                            oEvent.reservation = jsonInfo.getInt("reservation");
                            oEvent.payment = jsonInfo.getInt("payment");
                            oEvent.amount = jsonInfo.getDouble("amount");
                            oEvent.claimeoffer = jsonInfo.getInt("claimeoffer");
                            oEvent.pushable = jsonInfo.getInt("pushavailable");
                            oEvent.location = jsonInfo.getString("location");
                            oEvent.category = jsonInfo.getString("category");

                            oEvent.followerEmail = email;
                            oEvent.firstName = firstname;
                            oEvent.lastName = lastname;
                            Long tsLong = System.currentTimeMillis()/1000;
                            oEvent.readDate = tsLong.toString();

                            realm.commitTransaction();
                            realm.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

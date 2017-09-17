package com.readinsite.ranchlife.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.readinsite.ranchlife.utils.Validator;

/**
 * @author mbelsky 04.09.15
 */
public class UserPreferences {

    private static volatile UserPreferences instance;

    public static UserPreferences getInstance() {
        return getInstance(null);
    }

    static UserPreferences getInstance(final Context context) {
        if ( null == instance ) {
            synchronized ( UserPreferences.class ) {
                if ( null == instance ) {
                    instance = new UserPreferences(context);
                }
            }
        }
        return instance;
    }

    private static final String PREFERENCES_NAME = "com.mbelsky.ibis.android.app.UserPreferences.PREFERENCES";
    private static final String PREFERENCES_VERSION_KEY = "PREFERENCES_VERSION";
    private static final int PREFERENCES_VERSION = 2;
    private static final int DEFAULT_PREFERENCES_VERSION = -1;

    private static final String PICTURE_KEY = "PROFILE_PICTURE";
    private static final String NAME_KEY = "NAME";
    private static final String PWD_KEY = "PASSWORD";
    private static final String EMAIL_KEY = "EMAIL";
    private static final String DEVICE_ID_KEY = "DEVICE_ID";
    private static final String TEMPORARY_KEY = "TEMPORARY";
    private static final String SITE_ID_KEY = "SITE_ID";
    private static final String SITE_NAME_KEY = "SITE_NAME";
    private static final String STORAGE_NAME_KEY = "STORAGE_NAME";
    private static final String STORAGE_ID_KEY = "STORAGE_ID";
    private static final String SITE_LAT_KEY = "SITE_LAT";
    private static final String SITE_LONG_KEY = "SITE_LONG";
    private static final String WORK_TYPE_KEY = "WORK_TYPE";

    private static final String USER_TYPE_KEY = "USER_TYPE";

    private static final String CLIENT_TOKEN_KEY = "CLIENT_TOKEN";
    private static final String CLIENT_ID_KEY = "CLIENT_ID";

    private static final String FIREBASE_TOKEN_KEY = "FIREBASE_TOKEN";

    private static final String TRACKING_POSITION_KEY = "TRACKING_POSITION";
    private final SharedPreferences preferences;
    private boolean hasInitialized;

    private String profilePicture;
    private String name;
    private String password;
    private String email;
    private String userType;
    private String devcieId;
    private String clientToken;
    private int clientId;
    private long siteId;
    private int temporary;
    private String surname;
    private int storageId;
    private String storageName;
    private float siteLatitude;
    private float siteLongitude;
    private String workType;
    private String trackingPosition;

    public void reset() {
        setName("");
        setPwd("");
        setEmail("");
        setSurname("");
        setStorage(0, null, 0f, 0f);
        setClientId(0);
        setWorkType(null);
        setProfilePicture("");
        setClientToken("");
        setUserType("");
        setFirebaseToken("");
        setTrackingPositions("");
    }



    public boolean isClientUser() {
        return !TextUtils.isEmpty(clientToken);
    }
    public boolean isAdminUser() {
        return !TextUtils.isEmpty(userType) && userType.equals("admin");
    }

    public boolean isCheckedIn() {
        return !TextUtils.isEmpty(workType);
    }

    private UserPreferences(final Context context) {
        if ( null == context ) {
            throw new IllegalArgumentException("Context cannot be null.");
        }

        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        initialize();
    }

    private void initialize() {
        if ( hasInitialized ) {
            return;
        }

        hasInitialized = true;

        int preferencesVersion = preferences
                .getInt(PREFERENCES_VERSION_KEY, DEFAULT_PREFERENCES_VERSION);

        if ( PREFERENCES_VERSION != preferencesVersion ) {
            preferences.edit().clear()
                    .putInt(PREFERENCES_VERSION_KEY, PREFERENCES_VERSION)
                    .apply();
        }

        siteId = preferences.getLong(SITE_ID_KEY, -1L);
        clientId = preferences.getInt(CLIENT_ID_KEY, -1);
        storageId = preferences.getInt(STORAGE_ID_KEY, -1);
        storageName = preferences.getString(STORAGE_NAME_KEY, null);
        siteLatitude = preferences.getFloat(SITE_LAT_KEY, 0f);
        siteLongitude = preferences.getFloat(SITE_LONG_KEY, 0f);
        workType = preferences.getString(WORK_TYPE_KEY, null);

        name = preferences.getString(NAME_KEY, "");
        surname = preferences.getString(SITE_NAME_KEY, "");

        password = preferences.getString(PWD_KEY, "");
        email = preferences.getString(EMAIL_KEY, "");
        profilePicture = preferences.getString(PICTURE_KEY, "");
        clientToken = preferences.getString(CLIENT_TOKEN_KEY, "");
        userType = preferences.getString(USER_TYPE_KEY, "");
        devcieId = preferences.getString(DEVICE_ID_KEY, "");
        trackingPosition = preferences.getString(TRACKING_POSITION_KEY, "");
        temporary = preferences.getInt(TEMPORARY_KEY, 0);
    }

    //MARK: Getters & Setters

    public String getName() {
        return name;
    }

    public void setName(final @NonNull String name) {
        Validator.notNullOrThrow(name);
        this.name = name;
        preferences.edit()
                .putString(NAME_KEY, name)
                .apply();
    }

    public String getDeviceId() {
        return devcieId;
    }

    public void setDeviceId(final @NonNull String devcieId) {
        Validator.notNullOrThrow(devcieId);
        this.devcieId = devcieId;
        preferences.edit()
                .putString(DEVICE_ID_KEY, devcieId)
                .apply();
    }

    public void setPwd(final @NonNull String password) {
        this.password = password;
        preferences.edit()
                .putString(PWD_KEY, password)
                .apply();
    }
    public String getPwd() {
        return password;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(final @NonNull String name) {
        Validator.notNullOrThrow(name);
        this.email = name;
        preferences.edit()
                .putString(EMAIL_KEY, name)
                .apply();
    }

    public String getSurname() {
        return surname;
    }
    public String getStorageName() {
        return storageName;
    }
    public int getStorageId() {
        return storageId;
    }
    public long getSiteId() {
        return siteId;
    }

    public int getClientId() {
        return clientId;
    }

    public float getSiteLatitude() {
        return siteLatitude;
    }

    public float getSiteLongitude() {
        return siteLongitude;
    }

    public void setSurname(String surname) {
        this.surname = surname;
        preferences.edit()
                .putString(SITE_NAME_KEY, surname)
                .apply();
    }

    public void setTemporary(int temp) {
        this.temporary = temp;
        preferences.edit()
                .putInt(TEMPORARY_KEY, temp)
                .apply();
    }

    public int getTemporary() {
        return temporary;
    }
    public void setStorage(final int stId, final String stName, final double latitude,
                           final double longitude) {
        this.storageId = stId;
        this.storageName = stName;
        preferences.edit()
                .putInt(STORAGE_ID_KEY, stId)
                .putString(STORAGE_NAME_KEY, stName)
                .apply();
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
        preferences.edit()
                .putInt(CLIENT_ID_KEY, clientId)
                .apply();
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(final String workType) {
        this.workType = workType;
        preferences.edit()
                .putString(WORK_TYPE_KEY, workType)
                .apply();
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(final @NonNull String picture) {
        Validator.notNullOrThrow(picture);
        this.profilePicture = picture;
        preferences.edit()
                .putString(PICTURE_KEY, picture)
                .apply();
    }

    public void setClientToken(final @NonNull String token) {
        Validator.notNullOrThrow(token);
        this.clientToken = token;
        preferences.edit()
                .putString(CLIENT_TOKEN_KEY, token)
                .apply();
    }

    public void setUserType(final @NonNull String userType) {
        Validator.notNullOrThrow(userType);
        this.userType = userType;
        preferences.edit()
                .putString(USER_TYPE_KEY, userType)
                .apply();
    }

    public String getUserType() {
        return this.userType;
    }
    @NonNull
    synchronized public String getFirebaseToken() {
        return preferences.getString(FIREBASE_TOKEN_KEY, "");
    }

    synchronized public void setFirebaseToken(final @NonNull String firebaseToken) {
        Validator.notNullOrThrow(firebaseToken);
        preferences.edit()
                .putString(FIREBASE_TOKEN_KEY, firebaseToken)
                .apply();
    }

    public void setTrackingPositions(final @NonNull String positionStr) {
        Validator.notNullOrThrow(positionStr);
        this.trackingPosition = positionStr;
        preferences.edit()
                .putString(TRACKING_POSITION_KEY, positionStr)
                .apply();
    }

    public String getTrackingPositions() {
        return trackingPosition;
    }
}

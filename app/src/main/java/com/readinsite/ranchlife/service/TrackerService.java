package com.readinsite.ranchlife.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.readinsite.ranchlife.app.UserPreferences;
import com.readinsite.ranchlife.model.UserResponse;
import com.readinsite.ranchlife.rest.ApiClient;
import com.readinsite.ranchlife.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackerService extends Service {
	public static final String ACTION_UPDATE_LOCATION = "com.readinsite.ranchlife.service.TrackerService.UPDATE_LOCATION";
	public static final String EXTRA_LOCATION = "com.readinsite.ranchlife.service.TrackerService.LOCATION";

	private static final String TAG = "TURNBYTURN";
	private LocationManager mLocationManager = null;
	private static final int LOCATION_INTERVAL = 1000;
	private static final float LOCATION_DISTANCE = 100f;
	private static boolean isRunning = false;
	public static Location mLastLocation;
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final float LIMIST_DSITANCE = 1000f;
	public static final int LOCATION_DELAYED_TIME = 1800000; //ms
	private Location lastSentLocation;



	private class LocationListener implements android.location.LocationListener {

		public LocationListener(String provider) {
			Log.e(TAG, "LocationListener " + provider);
			mLastLocation = new Location(provider);
		}

		@Override
		public void onLocationChanged(Location location) {
			if (location == null)
				return;

			if (mLastLocation == null) {
				mLastLocation = new Location(location);
				sendLocation(location);
			} else
				mLastLocation.set(location);

			if (lastSentLocation == null) {
				sendLocationToServer();
			} else if (mLastLocation.distanceTo(lastSentLocation) > LIMIST_DSITANCE){
				checkLocation();
			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.e(TAG, "onProviderDisabled: " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.e(TAG, "onProviderEnabled: " + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e(TAG, "onStatusChanged: " + provider);
		}
	}

	LocationListener[] mLocationListeners = new LocationListener[]{
			new LocationListener(LocationManager.GPS_PROVIDER),
			new LocationListener(LocationManager.NETWORK_PROVIDER)
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public static boolean isRunning() {
		return isRunning;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
		mLastLocation = null;
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");

		isRunning = true;
		initializeLocationManager();

		try {
			boolean isGPSEnabled = mLocationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);


			boolean isNetworkEnabled = mLocationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				if (isGPSEnabled) {
					try {
						mLocationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
								mLocationListeners[0]);
					} catch (SecurityException ex) {
						Log.e(TAG, "fail to request location update, ignore", ex);
					} catch (IllegalArgumentException ex) {
						Log.e(TAG, "gps provider does not exist " + ex.getMessage());
					}
				}
				else if (isNetworkEnabled) {
					try {
						mLocationManager.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
								mLocationListeners[1]);
						} catch (SecurityException ex) {
						Log.e(TAG, "fail to request location update, ignore", ex);
						} catch (IllegalArgumentException ex) {
						Log.e(TAG, "network provider does not exist, " + ex.getMessage());
						}
				}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
		if (mLocationManager != null) {
			for (int i = 0; i < mLocationListeners.length; i++) {
				try {
					if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						return;
					}

					mLocationManager.removeUpdates(mLocationListeners[i]);
				} catch (Exception ex) {
					Log.e(TAG, "fail to remove location listners, ignore", ex);
				}
			}
		}
	}

	public static Location getLocation() {
		return mLastLocation;
	}

	private void checkLocation () {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				sendLocationToServer();
			}
		}, LOCATION_DELAYED_TIME);

	}
	private void sendLocation(Location location) {
		Intent displayImageIntent = new Intent(ACTION_UPDATE_LOCATION);
		displayImageIntent.putExtra(EXTRA_LOCATION, location);

		LocalBroadcastManager.getInstance(this).sendBroadcast(displayImageIntent);
	}

	private void sendLocationToServer() {
		if (lastSentLocation != null && mLastLocation.distanceTo(lastSentLocation) > LIMIST_DSITANCE) {
			return;
		}

		lastSentLocation = mLastLocation;

//		ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
//		UserPreferences preferences = UserPreferences.getInstance();
//
//		Call<UserResponse> call = apiService.updateUserlocation(preferences.getEmail(), String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), preferences.getDeviceId());
//		call.enqueue(new Callback<UserResponse>() {
//			@Override
//			public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//				Log.e("Dima", response.body().message);
//			}
//
//			@Override
//			public void onFailure(Call<UserResponse> call, Throwable t) {
//
//			}
//		});
	}
	private void initializeLocationManager() {
		Log.e(TAG, "initializeLocationManager");
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		}
	}

}

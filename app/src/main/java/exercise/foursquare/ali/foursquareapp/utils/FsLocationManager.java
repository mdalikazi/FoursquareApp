package exercise.foursquare.ali.foursquareapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by kazi_ on 8/25/2016.
 */
public class FsLocationManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {

    private static final String LOG_TAG = AppConstants.LOG_TAG_QUERY;

    private Activity mActivityContext;
    private GoogleApiClient mApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderApi mFusedLocationProviderApi;

    public FsLocationManager(Activity activity) {
        Log.i(LOG_TAG, "new FsLocationManager");
        mActivityContext = activity;
        mFusedLocationProviderApi = LocationServices.FusedLocationApi;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setFastestInterval(500)
                .setInterval(12000)
                .setMaxWaitTime(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mApiClient = new GoogleApiClient.Builder(mActivityContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void checkLocationSettings() {
        Log.i(LOG_TAG, "checkLocationSettings");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest).setAlwaysShow(true);
        LocationSettingsRequest request = builder.build();
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mApiClient, request);
        result.setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
//        https://developers.google.com/android/reference/com/google/android/gms/location/SettingsApi
//        https://developer.android.com/training/location/change-location-settings.html
        LocationSettingsStates states = locationSettingsResult.getLocationSettingsStates();
        Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.d(LOG_TAG, "LocationSettingsStatusCodes.SUCCESS");
                requestLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.d(LOG_TAG, "LocationSettingsStatusCodes.RESOLUTION_REQUIRED");
                try {
                    status.startResolutionForResult(mActivityContext, AppConstants.ENABLE_LOCATION_SETTINGS_DIALOG);
                } catch (IntentSender.SendIntentException e) {
                    Log.d(LOG_TAG, "startResolutionForResult exception. e: " + e.getMessage());
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.d(LOG_TAG, "LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE");
                //Nothing can be done
                break;
        }
        Log.d(LOG_TAG, "isGpsPresent: " + states.isGpsPresent());
        Log.d(LOG_TAG, "isGpsUsable: " + states.isGpsUsable());
        Log.d(LOG_TAG, "isLocationPresent: " + states.isLocationPresent());
        Log.d(LOG_TAG, "isLocationUsable: " + states.isLocationUsable());
    }

    public void connect() {
        Log.i(LOG_TAG, "Connect");
        if(!mApiClient.isConnected() || !mApiClient.isConnecting()) {
            mApiClient.connect();
        }
    }

    public void disconnect() {
        Log.i(LOG_TAG, "Disconnect");
        if (mApiClient != null && (mApiClient.isConnected() || mApiClient.isConnecting())) {
            try {
                mApiClient.disconnect();
                if (mFusedLocationProviderApi != null) {
                    mFusedLocationProviderApi.removeLocationUpdates(mApiClient, this);
                }
            } catch (IllegalStateException e) {
                Log.d(LOG_TAG, "IllegalStateException with mApiClient.disconnect: " + e.getMessage());
            }
        }
    }

    public void requestLocationUpdates() {
        Log.i(LOG_TAG, "requestLocationUpdates");
        try {
            mFusedLocationProviderApi.requestLocationUpdates(mApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.d(LOG_TAG, "Security Exception with location permission: " + e.getMessage());
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception with requestLocationUpdates: " + e.getMessage());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG_TAG, "onConnected");
        checkLocationSettings();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "onConnectionFailed. result: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, "onLocationChanged");
        sendLocation(location);
    }

    private void sendLocation(Location location) {
        Log.i(LOG_TAG, "sendLocation");
        Intent locationIntent = new Intent(AppConstants.LOCATION_FETCHED);
        locationIntent.putExtra(AppConstants.USER_LOCATION_LAT, location.getLatitude());
        locationIntent.putExtra(AppConstants.USER_LOCATION_LNG, location.getLongitude());
        LocalBroadcastManager.getInstance(mActivityContext).sendBroadcast(locationIntent);
    }
}

package exercise.foursquare.ali.foursquareapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by kazi_ on 8/25/2016.
 */
public class FsLocationManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String LOG_TAG = Constants.LOG_TAG_QUERY;

    private Activity mActivityContext;
    private GoogleApiClient mApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderApi mFusedLocationProviderApi;

    public FsLocationManager(Activity activity) {
        Log.i(LOG_TAG, "FsLocationManager");
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

    public void connect() {
        Log.i(LOG_TAG, "Connect");
        if(!mApiClient.isConnected() || !mApiClient.isConnecting()) {
            mApiClient.connect();
        }
    }

    public void disconnect() {
        Log.i(LOG_TAG, "Disconnect");
        if (mApiClient.isConnected() || mApiClient.isConnecting()) {
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG_TAG, "onConnected");
            try {
                mFusedLocationProviderApi.requestLocationUpdates(mApiClient, mLocationRequest, this);
            } catch (SecurityException e) {
                Log.d(LOG_TAG, "Security Exception with location: " + e.getMessage());
            }
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
        Intent locationIntent = new Intent(Constants.LOCATION_FETCHED);
        locationIntent.putExtra(Constants.USER_LOCATION_LAT, location.getLatitude());
        locationIntent.putExtra(Constants.USER_LOCATION_LNG, location.getLongitude());
        LocalBroadcastManager.getInstance(mActivityContext).sendBroadcast(locationIntent);
    }
}

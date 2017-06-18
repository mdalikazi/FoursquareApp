package exercise.foursquare.ali.foursquareapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
    private LocationManager mLocationManager;

    public FsLocationManager(Activity activity) {
        Log.d(LOG_TAG, "FsLocationManager");
        mActivityContext = activity;
            mApiClient = new GoogleApiClient.Builder(mActivityContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationRequest = new LocationRequest()
                .setFastestInterval(1000)
                .setInterval(2000)
                .setMaxWaitTime(10000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


    }

    public void connect() {
        Log.d(LOG_TAG, "Connect");
        if(!mApiClient.isConnected() || !mApiClient.isConnecting()) {
            mApiClient.connect();
        }
    }

    public void disconnect() {
        Log.d(LOG_TAG, "Disconnect");
        if (mApiClient.isConnected() || mApiClient.isConnecting()) {
            mApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "onConnected");
        if(ContextCompat.checkSelfPermission(mActivityContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mLocationRequest, this);
            LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            mApiClient.

        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(mActivityContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO: 8/31/2016 SHow explanation dialog
            } else {
                ActivityCompat.requestPermissions(mActivityContext,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.PERMISSION_ACCESS_FINE_LOCATION);
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "onLocationChanged");
        sendLocation(location);
    }

    private void sendLocation(Location location) {
        Intent locationIntent = new Intent(Constants.LOCATION_FETCHED);
        locationIntent.putExtra(Constants.USER_LOCATION_LAT, location.getLatitude());
        locationIntent.putExtra(Constants.USER_LOCATION_LNG, location.getLongitude());
        LocalBroadcastManager.getInstance(mActivityContext).sendBroadcast(locationIntent);
    }
}

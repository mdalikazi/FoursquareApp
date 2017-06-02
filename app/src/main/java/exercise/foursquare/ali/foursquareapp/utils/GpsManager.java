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
public class GpsManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = Constants.LOG_TAG_QUERY;
    private GoogleApiClient mApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    private Activity mActivityContext;

    public GpsManager(Activity activity) {
        Log.d(TAG, "GpsManager");
        mActivityContext = activity;
        if(ContextCompat.checkSelfPermission(mActivityContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mApiClient = new GoogleApiClient.Builder(mActivityContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationRequest = new LocationRequest()
                .setFastestInterval(2000)
                .setInterval(2000)
                .setMaxWaitTime(10000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(mActivityContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO: 8/31/2016 Show explanation dialog
            } else {
                ActivityCompat.requestPermissions(mActivityContext,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.PERMISSION_ACCESS_FINE_LOCATION);
            }
        }

    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_ACCESS_FINE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = true;
                } else {
                    mPermissionGranted = false;
                }
                break;
        }
    }*/

    public void connect() {
        Log.d(TAG, "Connect");
        if(mApiClient.isConnected() || mApiClient.isConnecting()) {
            mApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if(ContextCompat.checkSelfPermission(mActivityContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            /*Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            if(location != null) {
                sendLocation(location);
            }*/
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mLocationRequest, this);
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

    private void sendLocation(Location location) {
        Intent locationIntent = new Intent(Constants.LOCATION_FETCHED);
        locationIntent.putExtra(Constants.USER_LOCATION_LAT, location.getLatitude());
        locationIntent.putExtra(Constants.USER_LOCATION_LAT, location.getLongitude());
        LocalBroadcastManager.getInstance(mActivityContext).sendBroadcast(locationIntent);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        sendLocation(location);
    }
}

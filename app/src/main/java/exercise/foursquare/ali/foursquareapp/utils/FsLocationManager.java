package exercise.foursquare.ali.foursquareapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by kazi_ on 8/25/2016.
 */
public class FsLocationManager implements
        LocationListener,
        OnCompleteListener<LocationSettingsResponse> {

    private static final String LOG_TAG = AppConstants.LOG_TAG_FS_APP;

    private Activity mActivityContext;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationUpdateListener mLocationUpdateListener;
    private Snackbar mSnackbar;

    public FsLocationManager(Activity activity, LocationUpdateListener locationUpdateListener) {
        Log.i(LOG_TAG, "new FsLocationManager");
        mActivityContext = activity;
        mLocationUpdateListener = locationUpdateListener;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        mLocationUpdateListener.onLocationUpdate(location);
                    }
                } else {
                    super.onLocationResult(locationResult);
                }
            }
        };
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setFastestInterval(500)
                .setInterval(12000)
                .setMaxWaitTime(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        checkGooglePlayServices();
    }

    public void setSnackbar(Snackbar snackbar) {
        mSnackbar = snackbar;
    }

    private void checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int googlePlayServicesStatus = googleApiAvailability.isGooglePlayServicesAvailable(mActivityContext);
        if (googlePlayServicesStatus == ConnectionResult.SUCCESS) {
            Log.d(LOG_TAG, "googlePlayServicesStatus == ConnectionResult.SUCCESS");
        } else {
            Dialog updateDialog = googleApiAvailability.getErrorDialog(mActivityContext, googlePlayServicesStatus, 0);
            updateDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    mActivityContext.finish();
                }
            });
            updateDialog.show();
        }
    }

    public void checkLocationSettings() {
        Log.i(LOG_TAG, "checkLocationSettings");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(mActivityContext).checkLocationSettings(builder.build());
        result.addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
        try {
            LocationSettingsResponse response = task.getResult(ApiException.class);
            Log.d(LOG_TAG, "LocationSettingsResponse.SUCCESS");
            Log.d(LOG_TAG, "isGpsPresent: " + response.getLocationSettingsStates().isGpsPresent());
            Log.d(LOG_TAG, "isGpsUsable: " + response.getLocationSettingsStates().isGpsUsable());
            Log.d(LOG_TAG, "isLocationPresent: " + response.getLocationSettingsStates().isLocationPresent());
            Log.d(LOG_TAG, "isLocationUsable: " + response.getLocationSettingsStates().isLocationUsable());
            requestLocationUpdates();
//            getLastKnownLocation();
        } catch (ApiException exception) {
            switch (exception.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.d(LOG_TAG, "LocationSettingsStatusCodes.RESOLUTION_REQUIRED");
                    try {
                        // Cast to a resolvable exception.
                        ResolvableApiException resolvable = (ResolvableApiException) exception;
                        // Show the dialog by calling startResolutionForResult() and check the result in onActivityResult().
                        resolvable.startResolutionForResult(mActivityContext, AppConstants.ENABLE_LOCATION_SETTINGS_DIALOG);
                    } catch (IntentSender.SendIntentException e) {
                        Log.d(LOG_TAG, "startResolutionForResult SendIntentException. e: " + e.getMessage());
                    } catch (ClassCastException e) {
                        Log.d(LOG_TAG, "startResolutionForResult ClassCastException. e: " + e.getMessage());
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.d(LOG_TAG, "LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE");
                    //Nothing can be done
                    break;
            }
        }
    }

    public void connect() {
        Log.i(LOG_TAG, "Connect");
        checkLocationSettings();
    }

    public void disconnect() {
        Log.i(LOG_TAG, "Disconnect");
            try {
                if (mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }
                removeLocationUpdates();
            } catch (IllegalStateException e) {
                Log.d(LOG_TAG, "IllegalStateException with mApiClient.disconnect: " + e.getMessage());
            }
    }

    public void requestLocationUpdates() {
        Log.i(LOG_TAG, "requestLocationUpdates");
        try {
            if (!mSnackbar.isShown()) {
                mSnackbar.show();
            }
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        } catch (SecurityException e) {
            Log.d(LOG_TAG, "Security Exception with location permission: " + e.getMessage());
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception with requestLocationUpdates: " + e.getMessage());
        }
    }

    public void getLastKnownLocation() {
        Log.i(LOG_TAG, "getLastKnownLocation");

        try {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mLocationUpdateListener.onLocationUpdate(location);
                }
            });
        } catch (SecurityException e) {
            Log.d(LOG_TAG, "Security Exception with location permission: " + e.getMessage());
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception with getLastKnownLocation: " + e.getMessage());
        }
    }

    private void removeLocationUpdates() {
        Log.i(LOG_TAG, "removeLocationUpdates");
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, "onLocationUpdate");
        if (mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
        mLocationUpdateListener.onLocationUpdate(location);
    }

    public interface LocationUpdateListener {
        void onLocationUpdate(Location location);
    }
}

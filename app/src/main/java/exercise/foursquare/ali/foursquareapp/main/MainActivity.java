package exercise.foursquare.ali.foursquareapp.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.LinkedList;

import exercise.foursquare.ali.foursquareapp.R;
import exercise.foursquare.ali.foursquareapp.models.QueryResponse;
import exercise.foursquare.ali.foursquareapp.processor.QueryService;
import exercise.foursquare.ali.foursquareapp.utils.AppConstants;
import exercise.foursquare.ali.foursquareapp.utils.FsLocationManager;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = AppConstants.LOG_TAG_QUERY;

    private double mUserLocationLat;
    private double mUserLocationLng;
    private QueryService mQueryService;
    private QueryResponse mQueryResponse;
    private Gson mQueryResponseGsonObject;
    private String mQueryResponseString;
    private VenueAdapter mVenueAdapter;
    private FsLocationManager mLocationManager;
    private BroadcastReceiver mQueryBrodcastReceiver;
    private BroadcastReceiver mLocationBrodcastReceiver;
    private SimpleArrayMap<String, LinkedList> mVenues;

    private FloatingActionButton mLocationFab;
    private RecyclerView mRecyclerView;
    private Snackbar mSnackbar;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLocationFab = (FloatingActionButton) findViewById(R.id.fab_location);
        mRecyclerView = (RecyclerView) findViewById(R.id.venue_list_recycler_view);

        mVenues = new SimpleArrayMap<>();
        mLocationManager = new FsLocationManager(this);
        mQueryService = new QueryService();
        mQueryResponse = new QueryResponse();
        mQueryResponseGsonObject = new Gson();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSnackbar = Snackbar.make(mLocationFab, "Getting your location...", Snackbar.LENGTH_INDEFINITE)
                .setAction("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSnackbar.dismiss();
                        mLocationManager.disconnect();
                    }
                });

        mLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSnackbar.show();
                if (hasLocationPermission()) {
                    mLocationManager.connect();
                }
            }
        });

        mQueryBrodcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "Result received");
                mQueryResponseString = intent.getStringExtra(AppConstants.QUERY_RESPONSE);
                mQueryResponse = mQueryResponseGsonObject.fromJson(mQueryResponseString, QueryResponse.class);
                mVenueAdapter = new VenueAdapter(createAdapterData());
                mRecyclerView.setAdapter(mVenueAdapter);
            }
        };

        mLocationBrodcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mUserLocationLat = intent.getDoubleExtra(AppConstants.USER_LOCATION_LAT, 0);
                mUserLocationLng = intent.getDoubleExtra(AppConstants.USER_LOCATION_LNG, 0);
                mQueryService.startQueryService(MainActivity.this, "coffee", mUserLocationLat, mUserLocationLng);
                mSnackbar.dismiss();
                Toast.makeText(MainActivity.this, "Your location is: " + mUserLocationLat + "," + mUserLocationLng, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private SimpleArrayMap<String, LinkedList> createAdapterData() {
        mVenues = new SimpleArrayMap<>();
        mVenues.put(AppConstants.VENUE_NAME, mQueryResponse.getNames());
        mVenues.put(AppConstants.VENUE_ADDRESS, mQueryResponse.getFormattedAddresses());
        mVenues.put(AppConstants.VENUE_DISTANCE, mQueryResponse.getDistances());
        mVenues.put(AppConstants.VENUE_PHONE, mQueryResponse.getFormattedPhones());
        mVenues.put(AppConstants.VENUE_LOCATION, mQueryResponse.getLocations());
        mVenues.put(AppConstants.VENUE_HAS_MENU, mQueryResponse.getHaveMenus());
        mVenues.put(AppConstants.VENUE_MENU_URL, mQueryResponse.getMenuMobileUrls());
        return mVenues;
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mQueryBrodcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter queryFilter = new IntentFilter(AppConstants.QUERY_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mQueryBrodcastReceiver, queryFilter);

        IntentFilter locationFilter = new IntentFilter(AppConstants.LOCATION_FETCHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationBrodcastReceiver, locationFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean hasLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showLocationPermissionExplanation();
            } else {
                requestLocationPermission();
            }
            return false;
        }
    }

    private void showLocationPermissionExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please allow Location services to use this app.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestLocationPermission();
                    }
                })
                .show();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                AppConstants.PERMISSION_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConstants.PERMISSION_ACCESS_FINE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.connect();
                } else {
                    mSnackbar.dismiss();
                    mLocationManager.disconnect();
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, "onActivityResult");
        if (requestCode == AppConstants.ENABLE_LOCATION_SETTINGS_DIALOG) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(LOG_TAG, "RESULT_OK. Location enabled.");
                mLocationManager.requestLocationUpdates();
            } else {
                Log.d(LOG_TAG, "RESULT_CANCELED. Location disabled :(");
                mLocationManager.disconnect();
            }
        }
    }
}

package exercise.foursquare.ali.foursquareapp.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.LinkedList;
import exercise.foursquare.ali.foursquareapp.R;
import exercise.foursquare.ali.foursquareapp.models.QueryResponse;
import exercise.foursquare.ali.foursquareapp.processor.QueryService;
import exercise.foursquare.ali.foursquareapp.utils.Constants;
import exercise.foursquare.ali.foursquareapp.utils.LocationManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = Constants.LOG_TAG_QUERY;

    private QueryService mQueryService;
    private QueryResponse mQueryResponse;
    private Gson mQueryResponseGsonObject;
    private String mQueryResponseString;
    private BroadcastReceiver mQueryBrodcastReceiver;
    private BroadcastReceiver mLocationBrodcastReceiver;
    private SimpleArrayMap<String, LinkedList> mVenues;

    private FloatingActionButton mFab;
    private FloatingActionButton mLocationFab;
    private TextView lat;
    private TextView lng;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private VenueAdapter mVenueAdapter;
    private LocationManager mGpsManager;
    private double mUserLocationLat;
    private double mUserLocationLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mVenues = new SimpleArrayMap<>();
        getLocationManager();

        /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();*/

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mLocationFab = (FloatingActionButton) findViewById(R.id.fab_location);
        lat = (TextView) findViewById(R.id.lat);
        lng = (TextView) findViewById(R.id.lng);
        mRecyclerView = (RecyclerView) findViewById(R.id.venue_list_recycler_view);

        mQueryService = new QueryService();
        mQueryResponse = new QueryResponse();
        mQueryResponseGsonObject = new Gson();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGpsManager.connect();
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQueryService.startQueryService(MainActivity.this, "coffee", 40.7, -74);
            }
        });

        mQueryBrodcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Result received");
                mQueryResponseString = intent.getStringExtra(Constants.QUERY_RESPONSE);
                mQueryResponse = mQueryResponseGsonObject.fromJson(mQueryResponseString, QueryResponse.class);
                mVenueAdapter = new VenueAdapter(createAdapterData());
                mRecyclerView.setAdapter(mVenueAdapter);
            }
        };

        mLocationBrodcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mUserLocationLat = intent.getDoubleExtra(Constants.USER_LOCATION_LAT, 0);
                mUserLocationLng = intent.getDoubleExtra(Constants.USER_LOCATION_LNG, 0);
                Log.d(TAG, "User latitude: " + mUserLocationLat);
                Log.d(TAG, "User longitude: " + mUserLocationLng);
                lat.setText(String.valueOf(mUserLocationLat));
                lng.setText(String.valueOf(mUserLocationLng));
            }
        };
    }

    private SimpleArrayMap<String, LinkedList> createAdapterData() {
        mVenues = new SimpleArrayMap<>();
        mVenues.put(Constants.VENUE_NAME, mQueryResponse.getNames());
        mVenues.put(Constants.VENUE_ADDRESS, mQueryResponse.getFormattedAddresses());
        mVenues.put(Constants.VENUE_DISTANCE, mQueryResponse.getDistances());
        mVenues.put(Constants.VENUE_PHONE, mQueryResponse.getFormattedPhones());
        mVenues.put(Constants.VENUE_LOCATION, mQueryResponse.getLocations());
        mVenues.put(Constants.VENUE_HAS_MENU, mQueryResponse.getHaveMenus());
        mVenues.put(Constants.VENUE_MENU_URL, mQueryResponse.getMenuMobileUrls());
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
        IntentFilter queryFilter = new IntentFilter(Constants.QUERY_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mQueryBrodcastReceiver, queryFilter);

        IntentFilter locationFilter = new IntentFilter(Constants.LOCATION_FETCHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationBrodcastReceiver, locationFilter);
    }

    private void getLocationManager() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mGpsManager = new LocationManager(this);
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO: 8/31/2016 Show explanation dialog
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.PERMISSION_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_ACCESS_FINE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGpsManager = new LocationManager(this);
                    //mPermissionGranted = true;
                } else {
                    //mPermissionGranted = false;
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

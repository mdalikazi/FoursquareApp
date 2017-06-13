package exercise.foursquare.ali.foursquareapp.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import exercise.foursquare.ali.foursquareapp.utils.FsLocationManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = Constants.LOG_TAG_QUERY;

    private double mUserLocationLat;
    private double mUserLocationLng;
    private QueryService mQueryService;
    private QueryResponse mQueryResponse;
    private Gson mQueryResponseGsonObject;
    private String mQueryResponseString;
    private VenueAdapter mVenueAdapter;
    private FsLocationManager mGpsManager;
    private BroadcastReceiver mQueryBrodcastReceiver;
    private BroadcastReceiver mLocationBrodcastReceiver;
    private SimpleArrayMap<String, LinkedList> mVenues;
    private TextView lat;
    private TextView lng;

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

        lat = (TextView) findViewById(R.id.lat);
        lng = (TextView) findViewById(R.id.lng);
        mLocationFab = (FloatingActionButton) findViewById(R.id.fab_location);
        mRecyclerView = (RecyclerView) findViewById(R.id.venue_list_recycler_view);

        mVenues = new SimpleArrayMap<>();
        mGpsManager = new FsLocationManager(this);
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
                        mGpsManager.disconnect();
                    }
                });

        mLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSnackbar.show();
                mGpsManager.connect();
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
                lat.setText(String.valueOf(mUserLocationLat));
                lng.setText(String.valueOf(mUserLocationLng));
                mQueryService.startQueryService(MainActivity.this, "coffee", mUserLocationLat, mUserLocationLng);
                mSnackbar.dismiss();
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

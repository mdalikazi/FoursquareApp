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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
import exercise.foursquare.ali.foursquareapp.utils.AppConstants;
import exercise.foursquare.ali.foursquareapp.utils.FsLocationManager;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = AppConstants.LOG_TAG_QUERY;

    private double mUserLocationLat;
    private double mUserLocationLng;
    private String mSearchQuery;
    private VenueAdapter mVenueAdapter;
    private FsLocationManager mLocationManager;
    private BroadcastReceiver mQueryBrodcastReceiver;
    private BroadcastReceiver mLocationBrodcastReceiver;
    private SimpleArrayMap<String, LinkedList> mVenues;

    private QueryService mQueryService;
    private QueryResponse mQueryResponse;
    private Gson mQueryResponseGsonObject;
    private String mQueryResponseString;

    // Views
    private FloatingActionButton mLocationFab;
    private RecyclerView mRecyclerView;
    private Snackbar mSnackbar;
    private TextView mEmptyListMessage;
    private Toolbar mSearchViewRevealToolbar;
    private MenuItem mSearchMenuItem;

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
        mLocationManager = new FsLocationManager(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLocationFab = (FloatingActionButton) findViewById(R.id.fab_location);
        mRecyclerView = (RecyclerView) findViewById(R.id.main_activity_recycler_view);
        mEmptyListMessage = (TextView) findViewById(R.id.main_activity_empty_message);
        mSearchViewRevealToolbar = (Toolbar) findViewById(R.id.search_view_reveal_toolbar);
        setupSearchViewRevealToolbar();

        mVenues = new SimpleArrayMap<>();
        mQueryService = new QueryService();
        mQueryResponse = new QueryResponse();
        mQueryResponseGsonObject = new Gson();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        showEmptyMesssage(true);

        mSnackbar = Snackbar.make(mLocationFab, getString(R.string.snackbar_message_getting_your_location), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.cancel), new View.OnClickListener() {
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
                checkLocationPermissionAndConnect();
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

    private void showEmptyMesssage(boolean show) {
        if (show) {
            mEmptyListMessage.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyListMessage.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        IntentFilter queryFilter = new IntentFilter(AppConstants.QUERY_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mQueryBrodcastReceiver, queryFilter);

        IntentFilter locationFilter = new IntentFilter(AppConstants.LOCATION_FETCHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationBrodcastReceiver, locationFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mQueryBrodcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
        mLocationManager.disconnect();
    }

    private void setupSearchViewRevealToolbar() {
        mSearchViewRevealToolbar.inflateMenu(R.menu.menu_search);
        mSearchMenuItem = mSearchViewRevealToolbar.getMenu().findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(LOG_TAG, "onQueryTextChange newText: " + query);
                processSearch(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Disallow
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                animateSearchView(false);
                return true;
            }
        });

//        mSearchViewRevealToolbar.setNavigationIcon(R.mipmap.ic_navigation_arrow_back);
//        mSearchViewRevealToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(LOG_TAG, "mSearchViewRevealToolbar navigation click");
//                animateSearchView(true);
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home_search_icon:
                animateSearchView(true);
                mSearchMenuItem.expandActionView();
                break;
        }
        return true;
    }

    private void animateSearchView(boolean reveal) {
        //TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.toolbar));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnimationUtils.circleReveal(this, mSearchViewRevealToolbar, 0, true, reveal);
        }
    }

    private void processSearch(String query) {
        mSearchQuery = query;
        if (mUserLocationLat == 0 || mUserLocationLng == 0) {
            checkLocationPermissionAndConnect();
        } else {
            mQueryService.startQueryService(MainActivity.this, query, mUserLocationLat, mUserLocationLng);
        }
    }

    public void checkLocationPermissionAndConnect() {
        Log.i(LOG_TAG, "checkLocationPermissionAndConnect");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLocationManager.connect();
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showLocationPermissionExplanation();
            } else {
                requestLocationPermission();
            }
        }
    }

    private void showLocationPermissionExplanation() {
        Log.i(LOG_TAG, "showLocationPermissionExplanation");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.permission_message_location))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestLocationPermission();
                    }
                })
                .show();
    }

    private void requestLocationPermission() {
        Log.i(LOG_TAG, "requestLocationPermission");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                AppConstants.PERMISSION_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult");
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

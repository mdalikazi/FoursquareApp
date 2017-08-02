package exercise.foursquare.ali.foursquareapp.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import exercise.foursquare.ali.foursquareapp.R;
import exercise.foursquare.ali.foursquareapp.models.SearchResponse;
import exercise.foursquare.ali.foursquareapp.network.RequestsProcessor;
import exercise.foursquare.ali.foursquareapp.utils.AnimationUtils;
import exercise.foursquare.ali.foursquareapp.utils.AppConstants;
import exercise.foursquare.ali.foursquareapp.utils.FsLocationManager;

import static exercise.foursquare.ali.foursquareapp.R.id.main_activity_empty_message;

public class MainActivity extends AppCompatActivity implements
        FsLocationManager.LocationUpdateListener,
        RequestsProcessor.RequestResponseListener {

    private static final String LOG_TAG = AppConstants.LOG_TAG_QUERY;

    private double mUserLocationLat;
    private double mUserLocationLng;
    private boolean mSearchSubmitted;
    private String mSearchQuery;
    private VenueAdapter mVenueAdapter;
    private FsLocationManager mFsLocationManager;
    private RequestsProcessor mRequestsProcessor;

    // Views
    private MenuItem mSearchMenuItem;
    private Toolbar mSearchViewRevealToolbar;
    private AppBarLayout mSearchViewRevealAppBar;
    private FloatingActionButton mLocationFab;
    private Snackbar mGettingLocationSnackbar;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private TextView mEmptyListMessage;

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
        mFsLocationManager = new FsLocationManager(this, this);
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
        mEmptyListMessage = (TextView) findViewById(main_activity_empty_message);
        mProgressBar = (ProgressBar) findViewById(R.id.main_activity_progress_bar);
        mSearchViewRevealToolbar = (Toolbar) findViewById(R.id.search_view_reveal_toolbar);
        mSearchViewRevealAppBar = (AppBarLayout) findViewById(R.id.search_view_reveal_appbar_layout);
        setupSearchViewRevealToolbar();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mVenueAdapter = new VenueAdapter(MainActivity.this);
        mRecyclerView.setAdapter(mVenueAdapter);
        showEmptyMesssage(true);

        mGettingLocationSnackbar = Snackbar.make(mLocationFab, getString(R.string.snackbar_message_getting_your_location), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGettingLocationSnackbar.dismiss();
                        mFsLocationManager.disconnect();
                    }
                });

        mLocationFab.setVisibility(View.GONE);
        mLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGettingLocationSnackbar.show();
                checkLocationPermissionAndConnect();
            }
        });

        mRequestsProcessor = new RequestsProcessor(this, this);
    }

    private void showEmptyMesssage(boolean show) {
        if (show) {
            mEmptyListMessage.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mEmptyListMessage.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
        mFsLocationManager.disconnect();
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
                mSearchSubmitted = true;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnimationUtils.circleReveal(this, mSearchViewRevealAppBar, 0, true, reveal);
        } else {
            // TODO: 11/7/17 Test this
            TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.toolbar));
            mSearchViewRevealAppBar.setVisibility(reveal ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void processSearch(String query) {
        mSearchQuery = query;
        if (mUserLocationLat <= 0 || mUserLocationLng <= 0) {
            checkLocationPermissionAndConnect();
        } else {
            makeRequest(query);
        }
    }

    private void makeRequest(String query) {
        Log.i(LOG_TAG, "makeRequest");
        showEmptyMesssage(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mRequestsProcessor.searchQuery(query, mUserLocationLat, mUserLocationLng);
        mSearchSubmitted = false;
    }

    @Override
    public void responseOk(final SearchResponse searchResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "responseOk");
//                Log.d(LOG_TAG, "searchResponse: " + searchResponse.getNames().get(0));
                showEmptyMesssage(false);
                mProgressBar.setVisibility(View.GONE);
                mVenueAdapter.setSearchResponse(searchResponse);
                mRecyclerView.setAdapter(mVenueAdapter);
                mFsLocationManager.disconnect();
            }
        });
    }

    @Override
    public void responseError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "responseError");
                mEmptyListMessage.setText("Sorry, your search did not return any results. Please try again.");
                showEmptyMesssage(true);
                mProgressBar.setVisibility(View.GONE);
                mFsLocationManager.disconnect();
            }
        });
    }

    @Override
    public void onLocationUpdate(Location location) {
        mGettingLocationSnackbar.dismiss();
        mUserLocationLat = location.getLatitude();
        mUserLocationLng = location.getLongitude();
        if (mSearchSubmitted && mSearchQuery != null && !mSearchQuery.isEmpty()) {
            makeRequest(mSearchQuery);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(LOG_TAG, "onMapReady");


    }

    public void checkLocationPermissionAndConnect() {
        Log.i(LOG_TAG, "checkLocationPermissionAndConnect");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mFsLocationManager.connect();
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
                    mFsLocationManager.connect();
                } else {
                    mGettingLocationSnackbar.dismiss();
                    mFsLocationManager.disconnect();
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
//                mFsLocationManager.requestLocationUpdates();
                mFsLocationManager.getLastKnownLocation();
            } else {
                Log.d(LOG_TAG, "RESULT_CANCELED. Location disabled :(");
                mFsLocationManager.disconnect();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(LOG_TAG, "onBackPressed");
        // TODO: 17/7/17 close searchreveal with backpress then exit app
        if (mSearchViewRevealAppBar.getVisibility() == View.VISIBLE) {
            animateSearchView(false);
        } else {
            super.onBackPressed();
        }
    }
}

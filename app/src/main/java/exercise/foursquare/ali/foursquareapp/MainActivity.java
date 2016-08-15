package exercise.foursquare.ali.foursquareapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.google.gson.Gson;

import java.util.LinkedList;

import Models.QueryResponse;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = Constants.LOG_TAG_QUERY;
    private QueryService mQueryService;
    private QueryResponse mQueryResponse;
    private Gson mQueryResponseGsonObject;
    private String mQueryResponseString;
    private BroadcastReceiver mBrodcastReceiver;
    private SimpleArrayMap<String, LinkedList<String>> mVenues;

    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private VenueAdapter mVenueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mVenues = new SimpleArrayMap<>();

        /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();*/

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) findViewById(R.id.venue_list_recycler_view);

        mQueryService = new QueryService();
        mQueryResponse = new QueryResponse();
        mQueryResponseGsonObject = new Gson();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQueryService.startQueryService(MainActivity.this, "coffee", 40.7, -74);
            }
        });

        mBrodcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Result received");
                mQueryResponseString = intent.getStringExtra(Constants.QUERY_RESPONSE);
                mQueryResponse = mQueryResponseGsonObject.fromJson(mQueryResponseString, QueryResponse.class);
                mVenueAdapter = new VenueAdapter(createAdapterData());
                mRecyclerView.setAdapter(mVenueAdapter);
            }
        };

    }

    private SimpleArrayMap<String, LinkedList<String>> createAdapterData() {
        mVenues = new SimpleArrayMap<>();

        /*for(int i = 0; i < mQueryResponse.getVenues().size(); i++) {
            mVenues.put(Constants.VENUE_PHONE, mQueryResponse.getFormattedPhone(i));
            mVenues.put(Constants.VENUE_ADDRESS, mQueryResponse.getFormattedAddress(i));
            mVenues.put(Constants.VENUE_DISTANCE, String.valueOf(mQueryResponse.getDistance(i)));
            mVenues.put(Constants.VENUE_LAT, mQueryResponse.getLat(i));
            mVenues.put(Constants.VENUE_LANG, mQueryResponse.getLang(i));
            if(mQueryResponse.getHasMenu(i)) {
                mVenues.put(Constants.VENUE_MENU_URL, mQueryResponse.getMenuMobileUrl(i));
            } else {
                mVenues.put(Constants.VENUE_MENU_URL, null);
            }
        }*/

        mVenues.put(Constants.VENUE_NAME, mQueryResponse.getNames());
        return mVenues;
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBrodcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Constants.QUERY_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBrodcastReceiver, filter);
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

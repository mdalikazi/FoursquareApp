package exercise.foursquare.ali.foursquareapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import Models.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Exceptions";
    public static final String QUERY_COMPLETE = "query_complete";
    private QueryService mQueryService;
    private LocalBroadcastManager mBroadcastManager;
    private BroadcastReceiver mBrodcastReceiver;

    public static TextView mTvResponse;
    private Response mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTvResponse = (TextView) findViewById(R.id.response);

        /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/

        mQueryService = new QueryService();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQueryService.startQueryService(MainActivity.this, "coffee", 40.7, -74);
            }
        });

        /*IntentFilter filter = new IntentFilter(QUERY_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBrodcastReceiver, filter);*/
        mBrodcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Result received");
                mTvResponse.setText(intent.getStringExtra("response"));
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mBrodcastReceiver);
        this.unregisterReceiver(mBrodcastReceiver);
        //mBroadcastManager.unregisterReceiver(mBrodcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(QUERY_COMPLETE);
        this.registerReceiver(mBrodcastReceiver, filter);
        //LocalBroadcastManager.getInstance(this).registerReceiver(mBrodcastReceiver, filter);
        //mBroadcastManager.registerReceiver(mBrodcastReceiver, filter);
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

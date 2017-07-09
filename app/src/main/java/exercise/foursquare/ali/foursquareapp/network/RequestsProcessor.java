package exercise.foursquare.ali.foursquareapp.network;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import exercise.foursquare.ali.foursquareapp.R;
import exercise.foursquare.ali.foursquareapp.models.QueryResponse;
import exercise.foursquare.ali.foursquareapp.utils.AppConstants;
import exercise.foursquare.ali.foursquareapp.utils.NetConstants;

/**
 * Created by kazi_ on 7/21/2016.
 */
public class RequestsProcessor {
    //https://api.foursquare.com/v2/venues/search?client_id=CLIENT_ID&client_secret=CLIENT_SECRET&v=20130815&ll=40.7,-74&query=sushi

    private static final String LOG_TAG = AppConstants.LOG_TAG_QUERY;

    private Gson mGsonObject;
    private Context mContext;
    private Uri.Builder mUriBuilder;
    private ConnectionManager mConnectionManager;
    private BufferedInputStream mBufferedInputStream;
    private InputStreamReader mInputStreamReader;

    public RequestsProcessor(Context ctx) {
        mContext = ctx;
        mConnectionManager = new ConnectionManager(mContext);
    }

    public void getQuery(String query, double lat, double lang) {
        Resources res = mContext.getResources();
        String latLang = String.format(res.getString(R.string.param_lat_lang), lat, lang);
        mUriBuilder = new Uri.Builder();
        mUriBuilder.scheme(NetConstants.SCHEME_HTTPS)
                .authority(NetConstants.FS_AUTHORITY)
                .appendPath(NetConstants.FS_API_V2)
                .appendPath(NetConstants.FS_PATH_VENUES_SEARCH)
                .appendQueryParameter(NetConstants.FS_CLIENT_ID, AppConstants.CLIENT_ID)
                .appendQueryParameter(NetConstants.FS_CLIENT_SECRET, AppConstants.CLIENT_SECRET)
                .appendQueryParameter(NetConstants.FS_VERSION_PARAMETER, AppConstants.VERSION_PARAMTER)
                .appendQueryParameter(NetConstants.FS_LATITUDE_LONGITUDE, latLang)
                .appendQueryParameter(NetConstants.FS_PARAMETER_QUERY, query);

        try {
            URL url = new URL(mUriBuilder.build().toString());
            if (mConnectionManager != null) {
                HttpsURLConnection connection = mConnectionManager.get(url);
                if(connection != null && connection.getResponseCode() == NetConstants.RESPONSE_CODE_OK) {
                    mBufferedInputStream = new BufferedInputStream(connection.getInputStream());
                    mInputStreamReader = new InputStreamReader(mBufferedInputStream);
                    convertResponseToJson(mInputStreamReader);
                    mBufferedInputStream.close();
                    mInputStreamReader.close();
                }
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception with get. e: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void convertResponseToJson(InputStreamReader inputStreamReader) {
        mGsonObject =  new GsonBuilder().create();
        QueryResponse queryResponse = mGsonObject.fromJson(inputStreamReader, QueryResponse.class);
        sendQueryResponseBroadcast(queryResponse);
    }

    private void sendQueryResponseBroadcast(QueryResponse queryResponse) {
        Intent intent = new Intent(AppConstants.QUERY_COMPLETE);
        intent.putExtra(AppConstants.QUERY_RESPONSE, mGsonObject.toJson(queryResponse));
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}

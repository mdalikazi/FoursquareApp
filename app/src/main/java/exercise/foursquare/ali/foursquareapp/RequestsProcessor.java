package exercise.foursquare.ali.foursquareapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import Models.QueryResponse;

/**
 * Created by kazi_ on 7/21/2016.
 */
public class RequestsProcessor {
    //https://api.foursquare.com/v2/venues/search?client_id=CLIENT_ID&client_secret=CLIENT_SECRET&v=20130815&ll=40.7,-74&query=sushi
    /*and if link is simple like location uri, for example geo:0,0?q=29203
    Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",29203).build();*/
    private static final String CLIENT_ID = "ACAO2JPKM1MXHQJCK45IIFKRFR2ZVL0QASMCBCG5NPJQWF2G";
    private static final String CLIENT_SECRET = "YZCKUYJ1WHUV2QICBXUBEILZI1DMPUIDP5SHV043O04FKBHL";
    private static final String VERSION_PARAMTER = "20130815";
    private static final String TAG = "Exceptions";

    private Context mContext;
    private URL mUrl;
    private int mResponseCode;
    private HttpsURLConnection mConnection;
    private BufferedInputStream mBufferedInputStream;
    private InputStreamReader mInputStreamReader;
    private BufferedReader mBufferedReader;
    private Uri.Builder mUriBuilder;

    private StringBuilder mResponseContent;
    private JSONObject mResponseJson;
    private JsonReader mJsonReader;
    private Gson mGsonObject;


    public RequestsProcessor(Context ctx) {
        mContext = ctx;
    }

    public void getQuery(String query, double lat, double lang) {
        Resources res = mContext.getResources();
        String latLang = String.format(res.getString(R.string.param_lat_lang), lat, lang);
        mUriBuilder = new Uri.Builder();
        mUriBuilder.scheme("https")
                .authority("api.foursquare.com")
                .appendPath("v2")
                .appendPath("venues")
                .appendPath("search")
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("client_secret", CLIENT_SECRET)
                .appendQueryParameter("v", VERSION_PARAMTER)
                .appendQueryParameter("ll", latLang)
                .appendQueryParameter("query", query);

        try {
            mUrl = new URL(mUriBuilder.build().toString());
            Log.d(TAG, "mUrl: " + mUrl);

            mConnection = (HttpsURLConnection) mUrl.openConnection();
            mConnection.setDoInput(true);
            mConnection.setDoOutput(false);
            mConnection.setRequestMethod("GET");
            mConnection.setConnectTimeout(15000);
            mConnection.connect();
            mResponseCode = mConnection.getResponseCode();

            if(mResponseCode == 200) {
                mBufferedInputStream = new BufferedInputStream(mConnection.getInputStream());
                mInputStreamReader = new InputStreamReader(mBufferedInputStream);
                //mBufferedReader = new BufferedReader(mInputStreamReader);
                //mResponseContent = new StringBuilder();
                convertResponseToJson(mInputStreamReader);
                //mBufferedReader.close();
                mInputStreamReader.close();
                mBufferedInputStream.close();
            }
        } catch(Exception e) {
            Log.d(TAG, "IO Exception: " + e);
        } finally {
            if(mConnection != null) {
                mConnection.disconnect();
            }
        }
        /*String line;
                while((line = mBufferedReader.readLine()) != null) {
                    mResponseContent.append(line);
                }*/
        /*if(mResponseContent.length() != 0) {
            convertResponseFromStringToJson(mResponseContent.toString());
        }*/
    }

    private void convertResponseToJson(InputStreamReader inputStreamReader) {
        mGsonObject =  new GsonBuilder().create();
        QueryResponse queryResponse = mGsonObject.fromJson(inputStreamReader, QueryResponse.class);
        //Log.d(TAG, response.toString());
        sendBroadcast(queryResponse);
    }

    private void convertResponseFromStringToJson(String responseString) {
        mGsonObject =  new GsonBuilder().create();
        QueryResponse queryResponse = mGsonObject.fromJson(responseString, QueryResponse.class);
        sendBroadcast(queryResponse);
    }

    private void sendBroadcast(QueryResponse queryResponse) {
        Intent intent = new Intent(MainActivity.QUERY_COMPLETE);
        intent.putExtra(MainActivity.QUERY_RESPONSE, queryResponse.gsonToString());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}

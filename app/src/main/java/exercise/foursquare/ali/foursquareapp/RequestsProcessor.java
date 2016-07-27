package exercise.foursquare.ali.foursquareapp;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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

    private HttpsURLConnection mConnection;
    private BufferedInputStream mBufferedInputStream;
    private InputStreamReader mInputStreamReader;
    private BufferedReader mBufferedReader;
    private StringBuilder mResponseContent;
    private Uri.Builder mUriBuilder;
    private URL mUrl;
    private int mResponseCode;
    private Context mContext;

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
            mConnection.setDoOutput(false);
            mConnection.setRequestMethod("GET");
            mConnection.setConnectTimeout(15000);
            mConnection.connect();
            mResponseCode = mConnection.getResponseCode();

            if(mResponseCode == 200) {
                mBufferedInputStream = new BufferedInputStream(mConnection.getInputStream());
                mInputStreamReader = new InputStreamReader(mBufferedInputStream);
                mBufferedReader = new BufferedReader(mInputStreamReader);
                mResponseContent = new StringBuilder();

                String line;
                while((line = mBufferedReader.readLine()) != null) {
                    mResponseContent.append(line);
                }
                Log.d(TAG, "response content: " + mResponseContent);
                mBufferedReader.close();
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
    }
}

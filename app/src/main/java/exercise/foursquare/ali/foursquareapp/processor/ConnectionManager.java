package exercise.foursquare.ali.foursquareapp.processor;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import exercise.foursquare.ali.foursquareapp.utils.AppConstants;
import exercise.foursquare.ali.foursquareapp.utils.NetConstants;

/**
 * Created by alikazi on 28/6/17.
 */

public class ConnectionManager {

    public static final String LOG_TAG = AppConstants.LOG_TAG_QUERY;

    private Context mContext;
    private HttpsURLConnection mConnection;
    private BufferedInputStream mBufferedInputStream;

    public ConnectionManager(Context context) {
        mContext = context;
    }

    public InputStreamReader get(URL url) {
        Log.i(LOG_TAG, "get: " + url.toString());
        try  {
            mConnection = (HttpsURLConnection) url.openConnection();
            mConnection.setDoInput(true);
            mConnection.setDoOutput(false);
            mConnection.setRequestMethod(NetConstants.REQUEST_METHOD_GET);
            mConnection.setConnectTimeout(NetConstants.REQUEST_CONNECTION_TIMEOUT);
            mConnection.connect();
            if(mConnection.getResponseCode() == NetConstants.RESPONSE_CODE_OK) {
                mBufferedInputStream = new BufferedInputStream(mConnection.getInputStream());
                return new InputStreamReader(mBufferedInputStream);
            } else {
                return null;
            }
        } catch(Exception e) {
            Log.d(LOG_TAG, "IO Exception: " + e);
            return null;
        } finally {
            if(mConnection != null) {
                mConnection.disconnect();
            }
        }
    }

}

package exercise.foursquare.ali.foursquareapp.network;

import android.content.Context;
import android.util.Log;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import exercise.foursquare.ali.foursquareapp.utils.AppConstants;
import exercise.foursquare.ali.foursquareapp.utils.NetConstants;

/**
 * Created by alikazi on 28/6/17.
 */

public class ConnectionHelper {

    public static final String LOG_TAG = AppConstants.LOG_TAG_FS_APP;

    private Context mContext;
    private HttpsURLConnection mConnection;

    public ConnectionHelper(Context context) {
        mContext = context;
    }

    public HttpsURLConnection get(URL url) {
        Log.i(LOG_TAG, "get: " + url.toString());
        try {
            mConnection = (HttpsURLConnection) url.openConnection();
            mConnection.setDoInput(true);
            mConnection.setDoOutput(false);
            mConnection.setRequestMethod(NetConstants.REQUEST_METHOD_GET);
            mConnection.setConnectTimeout(NetConstants.REQUEST_CONNECTION_TIMEOUT);
            mConnection.connect();
            Log.i(LOG_TAG, "mConnection");
            return mConnection;
        } catch(Exception e) {
            Log.d(LOG_TAG, "Exception with get: " + e.toString());
            return null;
        }
//        finally {
//            if (mConnection != null) {
//                mConnection.disconnect();
//            }
//        }
    }

    public HttpsURLConnection post(URL url) {
        Log.i(LOG_TAG, "post: " + url.toString());
        try {
            mConnection = (HttpsURLConnection) url.openConnection();
            mConnection.setDoInput(true);
            mConnection.setDoOutput(true);
            mConnection.setRequestMethod(NetConstants.REQUEST_METHOD_POST);
            mConnection.setConnectTimeout(NetConstants.REQUEST_CONNECTION_TIMEOUT);
            mConnection.connect();
            return mConnection;
        } catch(Exception e) {
            return mConnection;
        } finally {
            if(mConnection != null) {
                mConnection.disconnect();
            }
        }
    }
}

package exercise.foursquare.ali.foursquareapp;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by kazi_ on 7/21/2016.
 */
public class RequestsProcessor {

    //https://api.foursquare.com/v2/venues/search?client_id=CLIENT_ID&client_secret=CLIENT_SECRET&v=20130815&ll=40.7,-74&query=sushi
    private static final String URI = "https://api.foursquare.com/v2/venues/search?";
    private static final String CLIENT_ID = "ACAO2JPKM1MXHQJCK45IIFKRFR2ZVL0QASMCBCG5NPJQWF2G";
    private static final String CLIENT_SECRET = "YZCKUYJ1WHUV2QICBXUBEILZI1DMPUIDP5SHV043O04FKBHL";
    private static final int VERSION_PARAMTER = 20130815;

    private HttpsURLConnection mConnection;
    private BufferedInputStream mInputStream;
    private BufferedOutputStream mBufferedOutputStream;
    private URL mUrl;

    public void getQuery(Context ctx, String query, float lat, float lang) {
        Resources res = ctx.getResources();
        String latLang = String.format(res.getString(R.string.param_lat_lang), lat, lang);
        mUrl = new URL(
                URI +
                        res.getString(R.string.param_client_id) +
                        res.getString(R.string.param_client_secret) +
                        res.getString(R.string.param_version) +
                        latLang +
                        res.getString(R.string.param_query)

        );

        //https://www.myawesomesite.com/turtles/types?type=1&sort=relevance#section-name
        //To build this with the Uri.Builder I would do the following.

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.myawesomesite.com")
                .appendPath("turtles")
                .appendPath("types")
                .appendQueryParameter("type", "1")
                .appendQueryParameter("sort", "relevance")
                .fragment("section-name");
        String myUrl = builder.build().toString();
    }
}

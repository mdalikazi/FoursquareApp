package exercise.foursquare.ali.foursquareapp.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import exercise.foursquare.ali.foursquareapp.utils.NetConstants;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class QueryService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    private static final String SERVICE_NAME = "QueryService";

    // TODO: Rename parameters
    private static final String QUERY = "QUERY";
    private static final String LAT = "LAT";
    private static final String LANG = "LANG";
    public static final String Listener ="Listener";

    private RequestsProcessor mRequestsProcessor;

    public QueryService() {
        super(SERVICE_NAME);
    }

    public void searchQuery(Context context, String query, double lat, double lang) {
        Intent intent = new Intent(context, QueryService.class);
        intent.setAction(NetConstants.REQUEST_METHOD_GET);
        intent.putExtra(QUERY, query);
        intent.putExtra(LAT, lat);
        intent.putExtra(LANG, lang);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(NetConstants.REQUEST_METHOD_GET)) {
                final String query = intent.getStringExtra(QUERY);
                final double lat = intent.getDoubleExtra(LAT, 0);
                final double lang = intent.getDoubleExtra(LANG, 0);
//                RequestsProcessor requestsProcessor = new RequestsProcessor(getApplicationContext()));
//                requestsProcessor.getQuery(query, lat, lang);
            } else if (action.equals(NetConstants.REQUEST_METHOD_POST)) {
                final String param1 = intent.getStringExtra(QUERY);
                final String param2 = intent.getStringExtra(LAT);
                handleActionFoo(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

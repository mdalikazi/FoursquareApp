package exercise.foursquare.ali.foursquareapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

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
    private static final String ACTION_GET = "GET";
    private static final String ACTION_POST = "POST";

    // TODO: Rename parameters
    private static final String QUERY = "QUERY";
    private static final String LAT = "LAT";
    private static final String LANG = "LANG";

    public QueryService() {
        super("QueryService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void getQuery(Context context, String query, double lat, double lang) {
        Intent intent = new Intent(context, QueryService.class);
        intent.setAction(ACTION_GET);
        intent.putExtra(QUERY, query);
        intent.putExtra(LAT, lat);
        intent.putExtra(LANG, lang);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET.equals(action)) {
                final String param1 = intent.getStringExtra(QUERY);
                final String param2 = intent.getStringExtra(LAT);
                RequestsProcessor.(param1, param2);
            } else if (ACTION_POST.equals(action)) {
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

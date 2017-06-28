package exercise.foursquare.ali.foursquareapp.utils;

/**
 * Created by alikazi on 26/6/17.
 */

public class NetConstants {

    // Foursquare APi
    public static final String FS_AUTHORITY = "api.foursquare.com";
    public static final String FS_API_V2 = "v2";
    public static final String FS_PATH_VENUES_SEARCH = "venues/search";

    // params
    public static final String FS_CLIENT_ID = "client_id";
    public static final String FS_CLIENT_SECRET = "client_secret";
    public static final String FS_VERSION_PARAMETER = "v";
    public static final String FS_LATITUDE_LONGITUDE = "ll";
    public static final String FS_PARAMETER_QUERY = "query";

    // request methods
    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";
    public static final String REQUEST_METHOD_PUT = "PUT";

    // General
    public static final String SCHEME_HTTPS = "https";
    public static final int RESPONSE_CODE_OK = 200;
    public static final int RESPONSE_CODE_ERROR = 404;
    public static final int REQUEST_CONNECTION_TIMEOUT = 15000;
}

package exercise.foursquare.ali.foursquareapp.utils;

/**
 * Created by kazi_ on 8/11/2016.
 */
public class AppConstants {

    //Foursquare
    public static final String CLIENT_ID = "L31DQPOQFTTFD5VBCADTCUJDAVMA42XHJR0WDPYABXHX03N2";
    public static final String CLIENT_SECRET = "AKGCIU1ZSGLTF31R20O3DCUNJAT5IRMOPGPRK5FAEJ1LG4Y4";
    public static final String VERSION_PARAMTER = "20130815";

    //Broadcast Intent extras
    public static final String QUERY_RESPONSE = "queryResponse";
    public static final String USER_LOCATION_LAT = "location_lat";
    public static final String USER_LOCATION_LNG = "location_lng";

    //Broadcast Intent filters
    public static final String QUERY_COMPLETE = "queryComplete";
    public static final String LOCATION_FETCHED = "locationFetched";

    //Model getter helpers
    public static final String VENUE_NAME = "venue_name";
    public static final String VENUE_PHONE = "venue_phone";
    public static final String VENUE_ADDRESS = "venue_address";
    public static final String VENUE_LOCATION = "venue_location";
    public static final String VENUE_DISTANCE = "venue_distance";
    public static final String VENUE_HAS_MENU = "venue_has_menu";
    public static final String VENUE_MENU_URL = "venue_menu_url";

    //Log tags
    public static final String LOG_TAG_QUERY = "Query";

    //Permission requestCodes
    public static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    public static final int ENABLE_LOCATION_SETTINGS_DIALOG = 2;
}

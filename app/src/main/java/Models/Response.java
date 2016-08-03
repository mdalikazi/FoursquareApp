package Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kazi_ on 7/28/2016.
 */
public class Response {

    private List<Venues> venues = new ArrayList<>();

    private class Venues {
        int id;
        String name;
        Contact contact;
        Location location;
        boolean verified;
        String url;
        boolean hasMenu;
        Menu menu;
    }

    private class Contact {
        String formattedPhone;
    }

    private class Location {
        String address;
        double lat;
        double lng;
        int distance;
        String city;
        String state;
        String formattedAdress[];
    }

    private class Menu {
        String mobileUrl;
    }

    public String gsonToString() {
        return venues.toString();
    }

}

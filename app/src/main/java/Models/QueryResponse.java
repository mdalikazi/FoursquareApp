package Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kazi_ on 7/28/2016.
 */
public class QueryResponse {

    private Meta meta;
    private Response response;

    private class Meta {
        int code;
        String requestId;
    }

    private class Response {
        List<Venues> venues = new ArrayList<>();
    }

    private class Venues {
        String id;
        String name;
        Contact contact;
        Location location;
        List<Categories> categories = new ArrayList<>();
        boolean verified;
        Stats stats;
        String url;
        boolean hasMenu;
        Menu menu;
        boolean allowMenuUrlEdit;
        Specials specials;
        VenuePage venuePage;
        String storeId;
        HereNow hereNow;
        String referralId;
        List<VenueChains> venueChains = new ArrayList<>();
        boolean hasPerk;
    }

    private class Contact {
        String phone;
        String formattedPhone;
        String twitter;
    }

    private class Location {
        String address;
        String crossStreet;
        double lat;
        double lng;
        int distance;
        String postalCode;
        String cc;
        String city;
        String state;
        String country;
        String[] formattedAdress = {};
    }

    private class Categories {
        String id;
        String name;
        String pluralName;
        String shortName;
        Icon icon;
        boolean primary;
    }

    private class Icon {
        String prefix;
        String suffix;
    }

    private class Stats {
        int checkinsCount;
        int usersCount;
        int tipCount;
    }

    private class Menu {
        String type;
        String label;
        String anchor;
        String url;
        String mobileUrl;
    }

    private class Specials {
        int count;
        String[] items = {};
    }

    private class VenuePage {
        String id;
    }

    private class HereNow {
        int count;
        String summary;
        List<Groups> groups = new ArrayList<>();
    }

    private class Groups {
        String type;
        String name;
        int count;
        String[] items = {};
    }

    private class VenueChains {
        String id;
    }

    public String gsonToString() {
        return response.venues.get(0).name;
    }

    public Response getResponse() {
        return response;
    }
}
package Models;

import java.util.ArrayList;
import java.util.LinkedList;
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

    public class Venues {
        String id;
        public String name;
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
        String[] formattedAddress = {};
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

    //GETTERS

    public String getName(int i) {
        return getVenues().get(i).name;
    }

    public String getFormattedPhone(int i) {
        return getVenues().get(i).contact.formattedPhone;
    }

    public String getFormattedAddress(int i) {
        return getVenues().get(i).location.formattedAddress.toString();
    }

    public String getLang(int i) {
        return String.valueOf(getVenues().get(i).location.lng);
    }

    public String getLat(int i) {
        return String.valueOf(getVenues().get(i).location.lat);
    }

    public int getDistance(int i) {
        return getVenues().get(i).location.distance;
    }

    public boolean getIsVerified(int i) {
        return getVenues().get(i).verified;
    }

    public boolean getHasMenu(int i) {
        return getVenues().get(i).hasMenu;
    }

    public String getMenuMobileUrl(int i) {
        return getVenues().get(i).menu.mobileUrl;
    }

    private Response getResponse() {
        return response;
    }

    public List<Venues> getVenues() {
        Response response = getResponse();
        return response.venues;
    }

    public LinkedList<String> getNames() {
        LinkedList<String> names = new LinkedList<>();
        for(int i = 0; i < getVenues().size(); i++) {
            names.add(getName(i));
        }
        return names;
    }

    public LinkedList<String> getFormattedPhone() {
        return null;
    }


    /*public LinkedList<String> getNames() {
        LinkedList<String> names = new LinkedList<>();
        Response response = getResponse();
        for(int i = 0; i < response.venues.size(); i++) {
            names.add(response.venues.get(i).name);
        }
        return names;
    }

    public LinkedList<String> getFormattedPhones() {
        LinkedList<String> formattedPhones = new LinkedList<>();
        Response response = getResponse();
        for(int i = 0; i < response.venues.size(); i++) {
            formattedPhones.add(response.venues.get(i).contact.formattedPhone);
        }
        return formattedPhones;
    }*/

}

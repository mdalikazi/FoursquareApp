package exercise.foursquare.ali.foursquareapp.models;

import android.location.LocationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kazi_ on 7/28/2016.
 */
public class SearchResponse {

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
        String message;
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

    //PRIVATE GETTERS

    private Response getResponse() {
        return response;
    }

    private List<Venues> getVenues() {
        Response response = getResponse();

        Collections.sort(response.venues, new Comparator<Venues>() {
            @Override
            public int compare(Venues venue1, Venues venue2) {
                if (venue1.location.distance < venue2.location.distance) {
                    return -1;
                } else if (venue1.location.distance > venue2.location.distance) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        return response.venues;
    }

    private String getName(int i) {
        return getVenues().get(i).name;
    }

    private String getFormattedPhone(int i) {
        return getVenues().get(i).contact.formattedPhone;
    }

    private String getFormattedAddress(int i) {
        StringBuilder formattedAddressFromArray = new StringBuilder();
        for(int j = 0; j < getVenues().get(i).location.formattedAddress.length-2; j++) {
            formattedAddressFromArray.append(getVenues().get(i).location.formattedAddress[j]);
            formattedAddressFromArray.append(" ");
        }
        return formattedAddressFromArray.toString();
    }

    private double getLang(int i) {
        return getVenues().get(i).location.lng;
    }

    private double getLat(int i) {
        return getVenues().get(i).location.lat;
    }

    private int getDistance(int i) {
        return getVenues().get(i).location.distance;
    }

    private boolean getIsVerified(int i) {
        return getVenues().get(i).verified;
    }

    private boolean getHasMenu(int i) {
        return getVenues().get(i).hasMenu;
    }

    private String getMenuMobileUrl(int i) {
        if(getHasMenu(i)) {
            return getVenues().get(i).menu.url;
        } else {
            return null;
        }
    }

    // PUBLIC GETTERS

    public LinkedList<String> getNames() {
        LinkedList<String> names = new LinkedList<>();
        for(int i = 0; i < getVenues().size(); i++) {
            names.add(getName(i));
        }
        return names;
    }

    public LinkedList<String> getFormattedPhones() {
        LinkedList<String> formattedPhones =  new LinkedList<>();
        for(int i = 0; i < getVenues().size(); i++) {
            formattedPhones.add(getFormattedPhone(i));
        }
        return formattedPhones;
    }

    public LinkedList<String> getFormattedAddresses() {
        LinkedList<String> formattedAddresses =  new LinkedList<>();
        for(int i = 0; i < getVenues().size(); i++) {
            formattedAddresses.add(getFormattedAddress(i));
        }
        return formattedAddresses;
    }

    public LinkedList<android.location.Location> getLocations() {
        LinkedList<android.location.Location> latLangs =  new LinkedList<>();
        android.location.Location eachLocation = new android.location.Location(LocationManager.GPS_PROVIDER);
        for(int i = 0; i < getVenues().size(); i++) {
            eachLocation.setLatitude(getLat(i));
            eachLocation.setLongitude(getLang(i));
            latLangs.add(eachLocation);
            eachLocation.reset();
        }
        return latLangs;
    }

    public LinkedList<Integer> getDistances() {
        LinkedList<Integer> distances =  new LinkedList<>();
        for(int i = 0; i < getVenues().size(); i++) {
            distances.add(getDistance(i));
        }
        return distances;
    }

    public LinkedList<Boolean> getAreVerified() {
        LinkedList<Boolean> areVerified =  new LinkedList<>();
        for(int i = 0; i < getVenues().size(); i++) {
            areVerified.add(getIsVerified(i));
        }
        return areVerified;
    }

    public LinkedList<Boolean> getHaveMenus() {
        LinkedList<Boolean> haveMenus =  new LinkedList<>();
        for(int i = 0; i < getVenues().size(); i++) {
            haveMenus.add(getHasMenu(i));
        }
        return haveMenus;
    }

    public LinkedList<String> getMenuMobileUrls() {
        LinkedList<String> menuMobileUrls =  new LinkedList<>();
        for(int i = 0; i < getVenues().size(); i++) {
            menuMobileUrls.add(getMenuMobileUrl(i));
        }
        return menuMobileUrls;
    }
}

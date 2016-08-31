package exercise.foursquare.ali.foursquareapp;

import android.location.Location;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

/**
 * Created by kazi_ on 8/4/2016.
 */
public class VenueAdapter extends RecyclerView.Adapter<VenueViewHolder> {

    private static final String TAG = Constants.LOG_TAG_QUERY;
    private SimpleArrayMap<String, LinkedList> mVenuesList = new SimpleArrayMap<>();
    private LinkedList<String> mNames;
    private LinkedList<String> mPhones;
    private LinkedList<String> mAddresses;
    private LinkedList<Location> mLocations;
    private LinkedList<Integer> mDistances;
    private LinkedList<Boolean> mHaveMenus;
    private LinkedList<String> mMenuUrls;


    public VenueAdapter(SimpleArrayMap<String, LinkedList> venuesList) {
        //Create list of lists
        mVenuesList = venuesList;
        mNames = new LinkedList<>(mVenuesList.get(Constants.VENUE_NAME));
        mDistances = new LinkedList<>(mVenuesList.get(Constants.VENUE_DISTANCE));
        mAddresses = new LinkedList<>(mVenuesList.get(Constants.VENUE_ADDRESS));
        mPhones = new LinkedList<>(mVenuesList.get(Constants.VENUE_PHONE));
        mLocations = new LinkedList<>(mVenuesList.get(Constants.VENUE_LOCATION));
        mHaveMenus = new LinkedList<>(mVenuesList.get(Constants.VENUE_HAS_MENU));
        mMenuUrls = new LinkedList<>(mVenuesList.get(Constants.VENUE_MENU_URL));
    }

    @Override
    public VenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new VenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VenueViewHolder holder, int position) {
        holder.getVenueTitle().setText(mNames.get(position));
        holder.getVenueDistance().setText(String.valueOf(mDistances.get(position)));
        holder.getVenueAddress().setText(mAddresses.get(position));
        holder.getVenuePhone().setText(mPhones.get(position));
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }
}

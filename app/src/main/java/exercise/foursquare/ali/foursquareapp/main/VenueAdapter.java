package exercise.foursquare.ali.foursquareapp.main;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import exercise.foursquare.ali.foursquareapp.R;
import exercise.foursquare.ali.foursquareapp.models.SearchResponse;
import exercise.foursquare.ali.foursquareapp.utils.AppConstants;

/**
 * Created by kazi_ on 8/4/2016.
 */
public class VenueAdapter extends RecyclerView.Adapter<VenueViewHolder> {

    private static final String LOG_TAG = AppConstants.LOG_TAG_QUERY;

    private Context mContext;
    private LinkedList<String> mNames;
    private LinkedList<String> mPhones;
    private LinkedList<String> mAddresses;
    private LinkedList<Location> mLocations;
    private LinkedList<Integer> mDistances;
    private LinkedList<Boolean> mHaveMenus;
    private LinkedList<String> mMenuUrls;

    public VenueAdapter(Context context, SearchResponse searchResponse) {
        mContext = context;
        mNames = new LinkedList<>(searchResponse.getNames());
        mDistances = new LinkedList<>(searchResponse.getDistances());
        mAddresses = new LinkedList<>(searchResponse.getFormattedAddresses());
        mPhones = new LinkedList<>(searchResponse.getFormattedPhones());
        mLocations = new LinkedList<>(searchResponse.getLocations());
        mHaveMenus = new LinkedList<>(searchResponse.getHaveMenus());
        mMenuUrls = new LinkedList<>(searchResponse.getMenuMobileUrls());
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

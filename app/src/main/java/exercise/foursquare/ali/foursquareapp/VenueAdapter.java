package exercise.foursquare.ali.foursquareapp;

import android.support.v4.util.SimpleArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kazi_ on 8/4/2016.
 */
public class VenueAdapter extends RecyclerView.Adapter<VenueViewHolder> {

    private SimpleArrayMap<String, String> mVenuesList = new SimpleArrayMap<>();

    public VenueAdapter(SimpleArrayMap<String, String> venuesList) {
        //Create list of lists
        mVenuesList = venuesList;

    }

    @Override
    public VenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new VenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VenueViewHolder holder, int position) {
        holder.getVenueTitle().setText(mVenuesList.get(Constants.VENUE_NAME));
    }

    @Override
    public int getItemCount() {
        return mVenuesList.size();
    }
}

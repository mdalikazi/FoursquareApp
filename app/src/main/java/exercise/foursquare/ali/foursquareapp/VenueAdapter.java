package exercise.foursquare.ali.foursquareapp;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.LinkedList;

/**
 * Created by kazi_ on 8/4/2016.
 */
public class VenueAdapter extends RecyclerView.Adapter<VenueViewHolder> {

    private LinkedList<String> mNamesList = new LinkedList<>();

    public VenueAdapter(LinkedList<String> names) {
        mNamesList = names;

    }

    @Override
    public VenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VenueViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mNamesList.size();
    }
}

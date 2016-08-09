package exercise.foursquare.ali.foursquareapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

/**
 * Created by kazi_ on 8/4/2016.
 */
public class VenueAdapter extends RecyclerView.Adapter<VenueViewHolder> {

    private LinkedList<String> mNamesList = new LinkedList<>();

    public VenueAdapter(LinkedList<String> names) {
        //Create list of lists
        mNamesList = names;

    }

    @Override
    public VenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new VenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VenueViewHolder holder, int position) {
        holder.getVenueTitle().setText(mNamesList.get(position));
    }

    @Override
    public int getItemCount() {
        return mNamesList.size();
    }
}

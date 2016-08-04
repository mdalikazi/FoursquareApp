package exercise.foursquare.ali.foursquareapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by kazi_ on 8/4/2016.
 */
public class VenueViewHolder extends RecyclerView.ViewHolder {

    private TextView mVenueTitle;

    public VenueViewHolder(View itemView) {
        super(itemView);

        mVenueTitle = (TextView) itemView.findViewById(R.id.venue_title);
    }

    public TextView getVenueTitle() {
        return mVenueTitle;
    }
}

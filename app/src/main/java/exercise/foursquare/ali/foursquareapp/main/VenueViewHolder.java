package exercise.foursquare.ali.foursquareapp.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import exercise.foursquare.ali.foursquareapp.R;

/**
 * Created by kazi_ on 8/4/2016.
 */
public class VenueViewHolder extends RecyclerView.ViewHolder {

    private TextView mVenueTitle;
    private TextView mVenueDistance;
    private TextView mVenueAddress;
    private TextView mVenuePhone;
    //private Button mGetDirections;

    public VenueViewHolder(View itemView) {
        super(itemView);

        mVenueTitle = (TextView) itemView.findViewById(R.id.venue_title);
        mVenueDistance = (TextView) itemView.findViewById(R.id.venue_distance);
        mVenueAddress = (TextView) itemView.findViewById(R.id.venue_address);
        mVenuePhone = (TextView) itemView.findViewById(R.id.venue_phone);
        //mGetDirections = (Button) itemView.findViewById(R.id.btn_get_directions);
    }

    public TextView getVenueTitle() {
        return mVenueTitle;
    }

    public TextView getVenueDistance() {
        return mVenueDistance;
    }

    public TextView getVenueAddress() {
        return mVenueAddress;
    }

    public TextView getVenuePhone() {
        return mVenuePhone;
    }

    /*public Button getButtonDirections() {
        return mGetDirections;
    }*/
}

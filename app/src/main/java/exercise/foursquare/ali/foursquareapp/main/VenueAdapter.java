package exercise.foursquare.ali.foursquareapp.main;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;

import exercise.foursquare.ali.foursquareapp.R;
import exercise.foursquare.ali.foursquareapp.models.SearchResponse;
import exercise.foursquare.ali.foursquareapp.utils.AnimationUtils;
import exercise.foursquare.ali.foursquareapp.utils.AppConstants;

/**
 * Created by kazi_ on 8/4/2016.
 */
public class VenueAdapter extends RecyclerView.Adapter<VenueViewHolder> implements OnMapReadyCallback {

    private static final String LOG_TAG = AppConstants.LOG_TAG_QUERY;

    private Activity mActivity;
    private int mInitialCardHeight;
    private int mAdapterPosition;
    private LinkedList<String> mNames;
    private LinkedList<String> mPhones;
    private LinkedList<String> mAddresses;
    private LinkedList<LatLng> mLocations;
    private LinkedList<Integer> mDistances;
    private LinkedList<Boolean> mHaveMenus;
    private LinkedList<String> mMenuUrls;
    private LinkedList<LatLng> mLatLngs;

    public VenueAdapter(Activity activity) {
        mActivity = activity;
        mNames = new LinkedList<>();
        mDistances = new LinkedList<>();
        mAddresses = new LinkedList<>();
        mPhones = new LinkedList<>();
        mLocations = new LinkedList<>();
        mHaveMenus = new LinkedList<>();
        mMenuUrls = new LinkedList<>();
        mLatLngs = new LinkedList<>();
    }

    public void setSearchResponse(SearchResponse searchResponse) {
        mNames.clear();
        mDistances.clear();
        mAddresses.clear();
        mPhones.clear();
        mLocations.clear();
        mHaveMenus.clear();
        mMenuUrls.clear();
        mLatLngs.clear();
        mNames = searchResponse.getNames();
        mDistances = searchResponse.getDistances();
        mAddresses = searchResponse.getFormattedAddresses();
        mPhones = searchResponse.getFormattedPhones();
        mLocations = searchResponse.getLocations();
        mHaveMenus = searchResponse.getHaveMenus();
        mMenuUrls = searchResponse.getMenuMobileUrls();
    }

    @Override
    public VenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.venue_list_item, parent, false);
        return new VenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VenueViewHolder holder, int position) {
        mAdapterPosition = holder.getAdapterPosition();

        if (mLocations.get(mAdapterPosition) != null) {
            double lat = mLocations.get(mAdapterPosition).latitude;
            double lng = mLocations.get(mAdapterPosition).longitude;
            mLatLngs.add(new LatLng(lat, lng));
        } else {
            Snackbar.make(holder.getVenueItemContainer(), R.string.snackbar_message_location_unavailable, Snackbar.LENGTH_SHORT);
        }

        holder.getVenueItemContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getVenueMap().getVisibility() == View.GONE) {
                    expandCard(v, holder.getVenueMap(), mLatLngs.get(mAdapterPosition));
                } else {
                    collapseCard(v, holder.getVenueMap());
                }
            }
        });

        holder.getVenueTitle().setText(mNames.get(mAdapterPosition));
        holder.getVenueDistance().setText(String.valueOf(mDistances.get(mAdapterPosition)));

        if (mAddresses.get(mAdapterPosition) != null) {
            holder.getVenueAddress().setText(mAddresses.get(mAdapterPosition));
        } else {
            holder.getVenueAddress().setVisibility(View.GONE);
        }

        if (mPhones.get(mAdapterPosition) != null) {
            holder.getVenuePhone().setText(mPhones.get(mAdapterPosition));
        } else {
            holder.getVenuePhone().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(LOG_TAG, "onMapReady");
        LatLng locationMark = mLatLngs.get(mAdapterPosition);
        googleMap.setMinZoomPreference(15);
//        googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(locationMark));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationMark));
    }

    private void expandCard(final View cardView, final MapView mapView, final LatLng latLng) {
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        cardView.measure(widthSpec, heightSpec);

        mInitialCardHeight = cardView.getHeight();
        mapView.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = AnimationUtils.valueAnimator(cardView.getHeight(), 1000, cardView);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("Location", latLng);
                mapView.onCreate(bundle);
                mapView.getMapAsync(VenueAdapter.this);
                mapView.onResume();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.start();
    }

    private void collapseCard(final View cardView, final View mapView) {
        ValueAnimator valueAnimator = AnimationUtils.valueAnimator(cardView.getHeight(), mInitialCardHeight, cardView);

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                mapView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        valueAnimator.start();
    }

}

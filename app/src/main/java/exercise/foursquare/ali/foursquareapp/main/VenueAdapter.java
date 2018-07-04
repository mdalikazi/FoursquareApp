package exercise.foursquare.ali.foursquareapp.main;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

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

    private static final String LOG_TAG = AppConstants.LOG_TAG_FS_APP;

    private Activity mActivity;
    private int mInitialCardHeight;
    private boolean mAnimationLocked;
    private LatLng mLatLngLocationMark;
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
        aniamteList(holder.itemView);
        int adapterPosition = holder.getAdapterPosition();

        if (mLocations.get(adapterPosition) != null) {
            double lat = mLocations.get(adapterPosition).latitude;
            double lng = mLocations.get(adapterPosition).longitude;
            mLatLngs.add(new LatLng(lat, lng));
        } else {
            Snackbar.make(holder.getVenueItemContainer(), R.string.snackbar_message_location_unavailable, Snackbar.LENGTH_SHORT);
        }

        holder.getVenueItemContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getVenueMap().getVisibility() == View.GONE) {
                    expandCard(v, holder.getVenueMap(), mLatLngs.get(holder.getLayoutPosition()));
                } else {
                    collapseCard(v, holder.getVenueMap());
                }
            }
        });

        holder.getVenueTitle().setText(mNames.get(adapterPosition));
        String distanceText = mActivity.getString(R.string.venue_list_item_distance_text, mDistances.get(adapterPosition));
        holder.getVenueDistance().setText(distanceText);

        if (mAddresses.get(adapterPosition) != null) {
            holder.getVenueAddress().setText(mAddresses.get(adapterPosition));
        }

        if (mPhones.get(adapterPosition) != null) {
            holder.getVenuePhone().setText(mPhones.get(adapterPosition));
        }

        if (mLocations.get(adapterPosition) != null) {
            holder.getVenueMapPlaceholder().setVisibility(View.GONE);
            double lat = mLocations.get(adapterPosition).latitude;
            double lng = mLocations.get(adapterPosition).longitude;
            mLatLngs.add(new LatLng(lat, lng));
        } else {
            holder.getVenueMapPlaceholder().setVisibility(View.VISIBLE);
            holder.getVenueMap().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(LOG_TAG, "onMapReady");
        googleMap.setMinZoomPreference(8);
//         TODO needs location permission check
//        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(mLatLngLocationMark));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLngLocationMark));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(20));
    }

    private void expandCard(final View cardView, final MapView mapView, final LatLng latLng) {
        mLatLngLocationMark = latLng;
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
                if (!mapView.isActivated()) {
                    mapView.onCreate(null);
                    mapView.getMapAsync(VenueAdapter.this);
                    mapView.onResume();
                    mapView.setActivated(true);
                }
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

    private void collapseCard(final View cardView, final MapView mapView) {
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

    @Override
    public void onViewDetachedFromWindow(VenueViewHolder holder) {
        holder.getVenueMap().setActivated(false);
        if (holder.getVenueMap().getVisibility() == View.VISIBLE) {
            collapseCard(holder.getVenueItemContainer(), holder.getVenueMap());
        }
    }

    private void aniamteList(View view) {
        if (!mAnimationLocked) {
            TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
            translateAnimation.setDuration(600);
            translateAnimation.setInterpolator(new DecelerateInterpolator());
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mAnimationLocked = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            view.startAnimation(translateAnimation);
        }

    }
}

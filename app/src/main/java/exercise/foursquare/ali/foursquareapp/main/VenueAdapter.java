package exercise.foursquare.ali.foursquareapp.main;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.LinkedList;

import exercise.foursquare.ali.foursquareapp.R;
import exercise.foursquare.ali.foursquareapp.models.SearchResponse;
import exercise.foursquare.ali.foursquareapp.utils.AnimationUtils;
import exercise.foursquare.ali.foursquareapp.utils.AppConstants;

/**
 * Created by kazi_ on 8/4/2016.
 */
public class VenueAdapter extends RecyclerView.Adapter<VenueViewHolder> {

    private static final String LOG_TAG = AppConstants.LOG_TAG_QUERY;

    private Context mContext;
    private int mInitialCardHeight;
    private LinkedList<String> mNames;
    private LinkedList<String> mPhones;
    private LinkedList<String> mAddresses;
    private LinkedList<Location> mLocations;
    private LinkedList<Integer> mDistances;
    private LinkedList<Boolean> mHaveMenus;
    private LinkedList<String> mMenuUrls;

    public VenueAdapter(Context context) {
        mContext = context;
        mNames = new LinkedList<>();
        mDistances = new LinkedList<>();
        mAddresses = new LinkedList<>();
        mPhones = new LinkedList<>();
        mLocations = new LinkedList<>();
        mHaveMenus = new LinkedList<>();
        mMenuUrls = new LinkedList<>();
    }

    public void setSearchResponse(SearchResponse searchResponse) {
        mNames.clear();
        mDistances.clear();
        mAddresses.clear();
        mPhones.clear();
        mLocations.clear();
        mHaveMenus.clear();
        mMenuUrls.clear();
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.venue_list_item, parent, false);
        return new VenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VenueViewHolder holder, int position) {
        holder.getVenueItemContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getVenueMap().getVisibility() == View.GONE) {
                    expandCard(v, holder.getVenueMap());
                } else {
                    collapseCard(v, holder.getVenueMap());
                }
            }
        });

        holder.getVenueTitle().setText(mNames.get(position));
        holder.getVenueDistance().setText(String.valueOf(mDistances.get(position)));
        holder.getVenueAddress().setText(mAddresses.get(position));
        holder.getVenuePhone().setText(mPhones.get(position));
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    private void expandCard(final View view, final ImageView map) {
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);

        mInitialCardHeight = view.getHeight();

        map.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = AnimationUtils.valueAnimator(view.getHeight(), 1000, view);
        valueAnimator.start();
    }

    private void collapseCard(final View view, final ImageView imageView) {
        ValueAnimator valueAnimator = AnimationUtils.valueAnimator(view.getHeight(), mInitialCardHeight, view);

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                imageView.setVisibility(View.GONE);
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

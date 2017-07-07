package exercise.foursquare.ali.foursquareapp.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewAnimationUtils;

import exercise.foursquare.ali.foursquareapp.R;

/**
 * Created by alikazi on 5/7/17.
 */

public class AnimationUtils {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void circleReveal(Context context, final View myView, int posFromRight, boolean containsOverflow, final boolean isShow) {
        int width = myView.getWidth();
        if(posFromRight > 0) {
            width -= (posFromRight*context.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (context.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
            //width -= (posFromRight*30) - (30/2);
        }

        if(containsOverflow) {
            width -= context.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);
        }

        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if(isShow) {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        } else {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);
        }

        anim.setDuration(400);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isShow) {
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if(isShow) {
            myView.setVisibility(View.VISIBLE);
        }

        anim.start();
    }
}

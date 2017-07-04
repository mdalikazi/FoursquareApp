package exercise.foursquare.ali.foursquareapp.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow)
    {
        final View myView = findViewById(viewID);

        int width=myView.getWidth();

        if(posFromRight>0)
            width<span class="pl-k">-=</span>(posFromRight<span class="pl-k">*</span>getResources()<span class="pl-k">.</span>getDimensionPixelSize(<span class="pl-smi">R</span><span class="pl-k">.</span>dimen<span class="pl-k">.</span>abc_action_button_min_width_material))<span class="pl-k">-</span>(getResources()<span class="pl-k">.</span>getDimensionPixelSize(<span class="pl-smi">R</span><span class="pl-k">.</span>dimen<span class="pl-k">.</span>abc_action_button_min_width_material)<span class="pl-k">/</span> <span class="pl-c1">2</span>);

        if(containsOverflow)
            width-=getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx=width;
        int cy=myView.getHeight()/2;

        Animator anim;
        if(isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0,(float)width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float)width, 0);

        anim.setDuration((long)220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isShow)
                {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if(isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();


    }
    Use the below code you can show and hide the SearchView.
// To reveal a previously invisible view

            circleReveal(R.id.searchtoolbar,1,true,true);

// To hide a previously visible view

    circleReveal(R.id.searchtoolbar,1,true,false);
}

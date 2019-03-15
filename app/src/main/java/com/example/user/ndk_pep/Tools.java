package com.example.user.ndk_pep;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Resources;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by user on 08-05-16.
 */
public class Tools {
    private static long showedToastTime= 0;

    public static void startAnimation(boolean isLayoutVisible, LinearLayout actionLayout, float value) {
        AnimatorSet animatorSet = new AnimatorSet();

        if (isLayoutVisible) {
            animatorSet.playTogether(
                    ObjectAnimator.ofPropertyValuesHolder(
                            actionLayout,
                            PropertyValuesHolder.ofFloat("translationY", 0, value))
                            .setDuration(500)
            );
        } else {
            animatorSet.playTogether(
                    ObjectAnimator.ofPropertyValuesHolder(
                            actionLayout,
                            PropertyValuesHolder.ofFloat("translationY", value, 0))
                            .setDuration(500)
            );
        }

        animatorSet.start();
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static void showToast(Context context, String text) {
        long currentTime = System.currentTimeMillis();
        if(Math.abs(currentTime - showedToastTime) > 300){
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
        showedToastTime = currentTime;
    }

}

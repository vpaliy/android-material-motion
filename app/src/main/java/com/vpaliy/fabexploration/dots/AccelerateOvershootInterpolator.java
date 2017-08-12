package com.vpaliy.fabexploration.dots;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

public class AccelerateOvershootInterpolator implements Interpolator {

    private OvershootInterpolator overshootInterpolator;
    private AccelerateInterpolator accelerateInterpolator;

    public AccelerateOvershootInterpolator(float tension){
        overshootInterpolator=new OvershootInterpolator(tension);
        accelerateInterpolator=new AccelerateInterpolator();
    }

    @Override
    public float getInterpolation(float input) {
        if(input>.95f) return overshootInterpolator.getInterpolation(input);
        return accelerateInterpolator.getInterpolation(input);
    }
}
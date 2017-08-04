package com.vpaliy.fabexploration;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionValues;

public class SwingTransition extends ChangeBounds {

    private ValueAnimator.AnimatorUpdateListener updateListener;

    public SwingTransition(){
        setPathMotion(new TransitionArcMotion());
    }

    public SwingTransition(Context context, AttributeSet attrs){
        super(context,attrs);
        setPathMotion(new TransitionArcMotion());
    }

    public void setUpdateListener(ValueAnimator.AnimatorUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        ValueAnimator animator=(ValueAnimator)super.createAnimator(sceneRoot, startValues, endValues);
        if(updateListener!=null) animator.addUpdateListener(updateListener);
        return animator;
    }
}

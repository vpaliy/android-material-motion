package com.vpaliy.fabexploration;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionValues;

public class ScaleTransition extends Transition {

    private float startScale;
    private float endScale;

    private static final String PROP_NAME="vpaliy:scale:x:y";

    public ScaleTransition(float startScale, float endScale){
        this.startScale=startScale;
        this.endScale=endScale;
    }

    public ScaleTransition(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public void setEndScale(float endScale) {
        this.endScale = endScale;
    }

    public void setStartScale(float startScale) {
        this.startScale = startScale;
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROP_NAME,startScale);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROP_NAME,endScale);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if(endValues.view!=null) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(endValues.view, View.SCALE_X,startScale,endScale);
            ObjectAnimator scaleY=ObjectAnimator.ofFloat(endValues.view,View.SCALE_Y,startScale,endScale);
            AnimatorSet animator=new AnimatorSet();
            animator.playTogether(scaleX,scaleY);
            return animator;
        }
        return null;
    }
}

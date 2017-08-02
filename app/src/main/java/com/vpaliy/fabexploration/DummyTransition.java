package com.vpaliy.fabexploration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.ViewGroup;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DummyTransition extends Transition{

    private final static String PROP_NAME="vpaliy:dummy:property";

    private AnimatorListenerAdapter listenerAdapter;

    public DummyTransition(){}

    public DummyTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListenerAdapter(AnimatorListenerAdapter listenerAdapter) {
        this.listenerAdapter = listenerAdapter;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROP_NAME,1);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROP_NAME,-1);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        AnimatorSet set=new AnimatorSet();
        set.addListener(listenerAdapter);
        return set;
    }

}

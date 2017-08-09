package com.vpaliy.fabexploration.dots;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;

import com.vpaliy.fabexploration.BaseFragment;
import com.vpaliy.fabexploration.R;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;


public class DotsFragment extends BaseFragment {

    @BindViews({R.id.first,R.id.second,R.id.third})
    protected List<FloatingActionButton> dots;

    @BindView(R.id.parent)
    protected ViewGroup parent;

    private boolean isFolded;

    @Override
    protected int mainRes() {
        return R.layout.fragment_dots;
    }


    @OnClick(R.id.first)
    public void revealFirst(FloatingActionButton dot){
        GradientDrawable drawable=GradientDrawable.class.cast(parent.getBackground());
        morphParent(drawable);
    }

    private void morphParent(GradientDrawable drawable){
        int endValue=isFolded?getResources().getDimensionPixelOffset(R.dimen.morph_radius):0;
        ObjectAnimator cornerAnimation = ObjectAnimator.ofFloat(drawable, "cornerRadius", endValue);
        endValue=isFolded?parent.getHeight()/2:parent.getHeight()*2;
        ValueAnimator heightAnimation = ValueAnimator.ofInt(parent.getHeight(),endValue);
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
                layoutParams.height = val;
                parent.setLayoutParams(layoutParams);
            }
        });

        AnimatorSet set=new AnimatorSet();
        set.playTogether(cornerAnimation,heightAnimation);
        set.setDuration(500);
        set.start();
        isFolded=!isFolded;
    }
}


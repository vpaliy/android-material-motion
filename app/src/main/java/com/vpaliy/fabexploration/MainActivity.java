package com.vpaliy.fabexploration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.SeekBar;

import java.util.List;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.codetail.animation.ViewAnimationUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG=MainActivity.class.getSimpleName();

    @BindView(R.id.fab)
    protected FloatingActionButton actionButton;

    @BindView(R.id.parent)
    protected ViewGroup parent;

    @BindView(R.id.background)
    protected View background;

    @BindView(R.id.controls_panel)
    protected ViewGroup panel;

    @BindViews({R.id.track_title,R.id.track_author})
    protected List<View> fadeViews;

    @BindView(R.id.seekbar)
    protected SeekBar seekBar;

    @BindView(R.id.divider)
    protected View divider;

    private Animator revealAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        seekBar.setProgress(30);
        panel.post(new Runnable() {
            @Override
            public void run() {
                int w = panel.getWidth();
                int h = panel.getHeight();
                final int endRadius = (int) Math.hypot(w, h);
                final int cx = panel.getWidth() / 2;
                final int cy = panel.getHeight()/2;
                panel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        revealAnimator=ViewAnimationUtils.createCircularReveal(panel, cx, cy, endRadius,0);
                        revealAnimator.removeAllListeners();
                        revealAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                panel.setVisibility(View.GONE);
                                backAnimation();
                                divider.animate()
                                        .setDuration(100)
                                        .scaleY(1).start();
                                revealAnimator=ViewAnimationUtils.createCircularReveal(panel, cx, cy, 0, endRadius);
                                revealAnimator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        panel.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        });
                        revealAnimator.setDuration(250);
                        revealAnimator.start();
                    }
                });
                revealAnimator = ViewAnimationUtils.createCircularReveal(panel, cx, cy, 0, endRadius);
                revealAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        panel.setVisibility(View.VISIBLE);
                       // for(View fadeView:fadeViews) fadeView.setVisibility(View.INVISIBLE);
                       // actionButton.setVisibility(View.INVISIBLE);
                    }
                });
                revealAnimator.setDuration(250);
                revealAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                panel.setVisibility(View.GONE);
            }
        });

    }

    @OnClick(R.id.fab)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onButtonClick(){
        TransitionArcMotion arcMotion=new TransitionArcMotion();
        ChangeBounds changeBounds=new ChangeBounds();
        changeBounds.setPathMotion(arcMotion);
        arcMotion.setCurveRadius(background.getHeight()/2);
        changeBounds.setDuration(200);
        changeBounds.addTarget(actionButton);
        changeBounds.setInterpolator(new AccelerateDecelerateInterpolator());
        TransitionSet set=new TransitionSet();
        set.addTransition(changeBounds);
        set.addListener(new TransitionAdapterListener(){
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionStart(transition);
             //   revealAnimator.start();

            }
        });
        TransitionManager.beginDelayedTransition(parent,set);

        revealAnimator.setStartDelay(100);
        revealAnimator.setDuration(250);
        seekBar.setProgress(30);
        ObjectAnimator progressAnimator=ObjectAnimator.ofInt(seekBar,"progress",30,10);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.setDuration(300);
        progressAnimator.setStartDelay(200);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.play(revealAnimator);
        animatorSet.play(progressAnimator);
        animatorSet.start();

        divider.animate()
                .setStartDelay(100)
                .setDuration(300)
                .scaleY(30).start();

        ConstraintLayout.LayoutParams params=ConstraintLayout.LayoutParams.class.cast(actionButton.getLayoutParams());
        params.leftToLeft=ConstraintLayout.LayoutParams.PARENT_ID;
        params.verticalBias+=0.1;
        actionButton.setLayoutParams(params);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void backAnimation(){
        TransitionArcMotion arcMotion=new TransitionArcMotion();
        ChangeBounds changeBounds=new ChangeBounds();
        changeBounds.setPathMotion(arcMotion);
        arcMotion.setCurveRadius(-background.getHeight()/2);
        changeBounds.setDuration(200);
        changeBounds.addTarget(actionButton);
        changeBounds.setInterpolator(new AccelerateDecelerateInterpolator());
        TransitionSet set=new TransitionSet();
        set.addTransition(changeBounds);
        TransitionManager.beginDelayedTransition(parent,set);
        ConstraintLayout.LayoutParams params=ConstraintLayout.LayoutParams.class.cast(actionButton.getLayoutParams());
        params.leftToLeft=ConstraintLayout.LayoutParams.UNSET;
        params.verticalBias-=0.1;
        actionButton.setLayoutParams(params);
    }
}

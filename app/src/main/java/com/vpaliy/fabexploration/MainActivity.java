package com.vpaliy.fabexploration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.codetail.animation.ViewAnimationUtils;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        panel.post(new Runnable() {
            @Override
            public void run() {
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
        changeBounds.setDuration(250);
        changeBounds.setInterpolator(new AccelerateInterpolator());
        changeBounds.addListener(new TransitionAdapterListener(){
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                int w = panel.getWidth();
                int h = panel.getHeight();
                int endRadius = (int) Math.hypot(w, h);
                int cx = (int) (panel.getX() + (panel.getWidth() / 2));
                int cy = (int) (panel.getY()) + panel.getHeight() / 2;
                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(panel, cx, cy, 0, endRadius);
                revealAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        actionButton.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        background.setVisibility(View.INVISIBLE);
                    }
                });
                panel.setVisibility(View.VISIBLE);
                for(View fadeView:fadeViews) fadeView.setVisibility(View.INVISIBLE);
                revealAnimator.setDuration(400);
                revealAnimator.setInterpolator(new AccelerateInterpolator());
                revealAnimator.start();
            }
        });
        TransitionManager.beginDelayedTransition(parent,changeBounds);
        ConstraintLayout.LayoutParams params=ConstraintLayout.LayoutParams.class.cast(actionButton.getLayoutParams());
        params.leftToLeft=ConstraintLayout.LayoutParams.PARENT_ID;
        params.verticalBias+=0.1;
        actionButton.setLayoutParams(params);
    }
}

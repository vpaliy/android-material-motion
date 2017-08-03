package com.vpaliy.fabexploration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import butterknife.BindView;
import butterknife.BindViews;
import android.annotation.TargetApi;
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

    @BindViews({R.id.album,R.id.track_author})
    protected List<View> fadeViews;

    @BindView(R.id.seekbar)
    protected SeekBar seekBar;

    @BindView(R.id.divider)
    protected View divider;

    @BindView(R.id.bottom_background)
    protected View bottomBackground;

    @BindView(R.id.track_title)
    protected TextView trackTitle;

    @BindView(R.id.sound_play)
    protected ImageView soundPlay;

    @BindView(R.id.action_bar)
    protected Toolbar actionBar;

    private Animator revealAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(actionBar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(null);
        }
        seekBar.setProgress(50);
        panel.post(new Runnable() {
            @Override
            public void run() {
                int w = panel.getWidth();
                int h = panel.getHeight();
                final int endRadius = (int) Math.hypot(w, h);
                final int cx = panel.getWidth() /2;
                final int cy = panel.getHeight()/2;
                panel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        revealAnimator=ViewAnimationUtils.createCircularReveal(panel, cx, cy, endRadius,0);
                        revealAnimator.removeAllListeners();
                        revealAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                actionButton.animate()
                                        .setDuration(200)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                actionButton.setVisibility(View.VISIBLE);
                                            }
                                        })
                                        .alpha(1).start();
                                for(final View view:fadeViews) {
                                    view.animate()
                                            .alpha(1)
                                            .setDuration(150)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);
                                                    view.setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                panel.setVisibility(View.GONE);
                                backAnimation();
                                divider.animate()
                                        .setDuration(100)
                                        .scaleY(1).start();
                                bottomBackground.setPivotY(0);
                                bottomBackground.animate()
                                        .setDuration(100)
                                        .scaleY(0).start();
                                soundPlay.animate().scaleY(0)
                                        .scaleX(0)
                                        .setDuration(20)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                trackTitle.setTextColor(Color.GRAY);
                                                soundPlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_volume_bottom));
                                                soundPlay.animate().scaleX(1)
                                                        .scaleY(1)
                                                        .setDuration(75)
                                                        .setListener(null).start();
                                            }
                                        }).start();
                                setUpReveal();
                            }
                        });
                        revealAnimator.setDuration(250);
                        revealAnimator.start();
                    }
                });
                setUpReveal();
                panel.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    private void setUpReveal(){
        int w = panel.getWidth();
        int h = panel.getHeight();
        final int endRadius = (int) Math.hypot(w, h);
        final int cx = panel.getWidth() /2;
        final int cy = panel.getHeight()/2;
        revealAnimator = ViewAnimationUtils.createCircularReveal(panel, cx, cy, 0, endRadius);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                panel.setVisibility(View.VISIBLE);
                actionButton.animate()
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                actionButton.setVisibility(View.INVISIBLE);
                            }
                        }).alpha(0).start();
                for(final View view:fadeViews){
                    view.animate()
                            .alpha(0)
                            .setDuration(100)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    view.setVisibility(View.INVISIBLE);
                                }
                            });
                }
            }
        });
        revealAnimator.setDuration(250);
        revealAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
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
        TransitionManager.beginDelayedTransition(parent,changeBounds);

        //reveal and animate the thumb
        runRevealNProgress();
        //stretch out the top divider
        runTopDividerScale();
        //expand the bottom divider
        runBottomDividerScale();
        //scale icon, swap icon, scale icon
        runIconScale();

        ConstraintLayout.LayoutParams params=ConstraintLayout.LayoutParams.class.cast(actionButton.getLayoutParams());
        params.leftToLeft=ConstraintLayout.LayoutParams.PARENT_ID;
        params.verticalBias+=0.1;
        actionButton.setLayoutParams(params);
    }

    private void runRevealNProgress(){
        revealAnimator.setStartDelay(100);
        revealAnimator.setDuration(375);
        revealAnimator.setInterpolator(new DecelerateInterpolator());
        seekBar.setProgress(50);
        ObjectAnimator progressAnimator=ObjectAnimator.ofInt(seekBar,"progress",50,20);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.setDuration(300);
        progressAnimator.setStartDelay(200);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.play(revealAnimator);
        animatorSet.play(progressAnimator);
        animatorSet.start();
    }

    private void runIconScale(){
        soundPlay.animate()
                .scaleY(0)
                .scaleX(0)
                .setDuration(20)
                .setStartDelay(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        trackTitle.setTextColor(Color.WHITE);
                        soundPlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_play_bottom));
                        soundPlay.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(150).setListener(null).start();
                    }
                }).start();
    }

    private void runBottomDividerScale(){
        bottomBackground.setPivotY(0);
        bottomBackground.animate()
                .setStartDelay(100)
                .setDuration(375)
                .scaleY(100).start();
    }

    private void runTopDividerScale(){
        divider.animate()
                .setStartDelay(100)
                .setDuration(375)
                .scaleY(30).start();
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
        TransitionManager.beginDelayedTransition(parent,changeBounds);
        ConstraintLayout.LayoutParams params=ConstraintLayout.LayoutParams.class.cast(actionButton.getLayoutParams());
        params.leftToLeft=ConstraintLayout.LayoutParams.UNSET;
        params.verticalBias-=0.1;
        actionButton.setLayoutParams(params);
    }
}

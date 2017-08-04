package com.vpaliy.fabexploration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

public class MainActivity extends AppCompatActivity {

    //TODO adapt to smaller screens
    //TODO change fonts, maybe colors
    //TODO generalize

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

    @BindView(R.id.play_pause)
    protected ImageView playPause;

    @BindView(R.id.next)
    protected ImageView next;

    @BindView(R.id.prev)
    protected ImageView prev;

    @BindView(R.id.controls)
    protected View revealContainer;

    private Animator revealAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpActionBar();
        setUpPanel();
        setUpButton();
    }

    private void setUpButton(){
        divider.post(()->{
            float offsetY=divider.getY()-(actionButton.getY()+actionButton.getHeight()/2);
            actionButton.setTranslationY(offsetY);
        });
    }

    private void setUpPanel(){
        panel.post(()->{
            int w = panel.getWidth();
            int h = panel.getHeight();
            final int endRadius = (int) Math.hypot(w, h);
            final int cx = panel.getWidth() /2;
            final int cy = panel.getHeight()/2;
            panel.setOnClickListener(v->{
                setUpPlayDrawable();
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
                        fadeInOutViews(1,150);
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
                        runIconScale(0,R.drawable.ic_volume_bottom,
                                ContextCompat.getColor(getApplicationContext(),R.color.color_grey));
                        setUpReveal();
                    }
                });
                revealAnimator.setDuration(250);
                revealAnimator.start();
            });
            setUpReveal();
            panel.setVisibility(View.GONE);
        });
    }

    private void setUpActionBar(){
        setSupportActionBar(actionBar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(null);
        }
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
        final float offsetY=(actionButton.getY()+actionButton.getHeight()/2)-divider.getTop();
        final int cx = (int)(actionButton.getX());
        final int cy = (int)(offsetY);
        revealAnimator = ViewAnimationUtils.createCircularReveal(panel, cx, cy, actionButton.getHeight()/2, endRadius);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                panel.setVisibility(View.VISIBLE);
                actionButton.setVisibility(View.INVISIBLE);
                playPause.setTranslationX(deltaX(playPause));
                playPause.setTranslationY(deltaY(playPause));

                playPause.animate()
                        .setDuration(animation.getDuration()/3)
                        .setInterpolator(animation.getInterpolator())
                        .translationX(0).translationY(0)
                        .start();

                fadeInOutViews(0,100);
            }

            private float deltaX(View view){
                return cx-(view.getLeft()+view.getWidth()/2);
            }

            private float deltaY(View view){
                return cy-(view.getTop()+view.getHeight()/2);
            }
        });
        revealAnimator.setDuration(250);
        revealAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }


    private void setUpPauseDrawable(){
        Drawable drawable=ContextCompat.getDrawable(this,R.drawable.ic_pause);
        actionButton.setImageDrawable(drawable);
        playPause.setImageDrawable(drawable);
    }

    private void setUpPlayDrawable(){
        Drawable drawable=ContextCompat.getDrawable(this,R.drawable.ic_play);
        actionButton.setImageDrawable(drawable);
        playPause.setImageDrawable(drawable);
    }

    @OnClick(R.id.fab)
    public void onButtonClick(){
        setUpPauseDrawable();
        TransitionArcMotion arcMotion=new TransitionArcMotion();
        SwingTransition swingTransition=new SwingTransition();
        arcMotion.setCurveRadius(background.getHeight()/2);
        swingTransition.setPathMotion(arcMotion);
        swingTransition.setDuration(200);
        swingTransition.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private volatile boolean isFired;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(!isFired){
                    if(animation.getAnimatedFraction()>0.55f){
                        isFired=true;
                        setUpReveal();
                        //reveal and animate the thumb
                        runRevealNProgress();
                        //stretch out the top divider
                        runTopDividerScale();
                        //expand the bottom divider
                        runBottomDividerScale();
                        //scale icon, swap icon, scale icon
                        runIconScale(0,R.drawable.ic_play_bottom,Color.WHITE);
                    }
                }
            }
        });
        swingTransition.addTarget(actionButton);
        swingTransition.setInterpolator(new AccelerateDecelerateInterpolator());
        TransitionManager.beginDelayedTransition(parent,swingTransition);


        ConstraintLayout.LayoutParams params=ConstraintLayout.LayoutParams.class.cast(actionButton.getLayoutParams());
        params.leftToLeft=ConstraintLayout.LayoutParams.PARENT_ID;
        params.verticalBias+=0.1;
        actionButton.setLayoutParams(params);
    }

    private void runRevealNProgress(){
        revealAnimator.setDuration(355);
        revealAnimator.setInterpolator(new DecelerateInterpolator());
        seekBar.setProgress(50);
        ObjectAnimator progressAnimator=ObjectAnimator.ofInt(seekBar,"progress",50,20);
        ObjectAnimator scaleY=ObjectAnimator.ofFloat(seekBar,View.SCALE_Y,0,1f);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.setDuration(300);
        scaleY.setDuration(300);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.play(revealAnimator);
        animatorSet.play(progressAnimator).with(scaleY);
        animatorSet.start();
    }

    private void fadeInOutViews(int alpha, int duration){
        for(final View view:fadeViews) {
            view.animate()
                    .alpha(alpha)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(alpha < 1 ? View.INVISIBLE : View.VISIBLE);
                        }
                    }).start();
        }
    }

    private void runIconScale(int delay, @DrawableRes int drawable, int color){
        soundPlay.animate()
                .scaleY(0)
                .scaleX(0)
                .setDuration(20)
                .setStartDelay(delay)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        trackTitle.setTextColor(color);
                        soundPlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,drawable));
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
                .setDuration(375)
                .scaleY(100).start();
    }

    private void runTopDividerScale(){
        divider.animate()
                .setDuration(375)
                .scaleY(30).start();
    }

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

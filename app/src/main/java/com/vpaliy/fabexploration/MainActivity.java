package com.vpaliy.fabexploration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

public class MainActivity extends AppCompatActivity {

    //TODO adapt to smaller screens
    //TODO change fonts, maybe colors
    //TODO generalize

    private static final String TAG=MainActivity.class.getSimpleName();

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
    protected RevealFrameLayout revealContainer;

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
            panel.setOnClickListener(v->{
                int w = panel.getWidth();
                int h = panel.getHeight();
                final int endRadius = (int) Math.hypot(w, h);
                final float offsetY=(actionButton.getY()+actionButton.getHeight()/2)-divider.getTop();
                final int cx = (int)(actionButton.getX()+actionButton.getWidth()/2);
                final int cy = (int)(offsetY);
                setUpPlayDrawable();
                revealAnimator=ViewAnimationUtils.createCircularReveal(panel, cx, cy, endRadius,actionButton.getHeight()/2);
                revealAnimator.removeAllListeners();
                revealAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        ViewCompat.setElevation(actionButton,0);
                        fadeInOutViews(1,150);
                        actionButton.setVisibility(View.VISIBLE);
                        actionButton.animate()
                                .alpha(1)
                                .setDuration(100)
                                .setListener(null)
                                .start();
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
                revealAnimator.setDuration(100);
                revealAnimator.start();
            });
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
        final int cx = (int)(actionButton.getX()+actionButton.getWidth()/2);
        final int cy = (int)(actionButton.getY()+actionButton.getHeight()/2-background.getTop());

        final float deltaX=cx-(playPause.getLeft()+playPause.getWidth()/2);
        final float deltaY=(cy-getResources().getDimension(R.dimen.play_pause_size)/2)-(playPause.getTop());
        playPause.setTranslationX(deltaX);
        playPause.setTranslationY(deltaY);
        revealAnimator = ViewAnimationUtils.createCircularReveal(panel, cx, cy, actionButton.getHeight(), endRadius);

        //
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                panel.setVisibility(View.VISIBLE);
                actionButton.setVisibility(View.INVISIBLE);
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

    private void runButtonAnimation(){
        next.setVisibility(View.INVISIBLE);prev.setVisibility(View.INVISIBLE);
        Path arcPath=createArcPath(playPause,0,0,-playPause.getTranslationY());
        ValueAnimator pathAnimator=ValueAnimator.ofFloat(0,1);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float point[] = new float[2];
            private PathMeasure pathMeasure = new PathMeasure(arcPath, false);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value = animation.getAnimatedFraction();
                // Gets the point at the fractional path length
                pathMeasure.getPosTan(pathMeasure.getLength() * value, point, null);

                // Sets view location to the above point
                playPause.setTranslationX(point[0]);
                playPause.setTranslationY(point[1]);
            }
        });
        pathAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        pathAnimator.setDuration(150);
        pathAnimator.start();
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
        final float playPauseY=playPause.getY()+background.getY();
        float endX=background.getWidth()/2;
        float endY=playPauseY+playPause.getHeight()/2;
        float startX=0;
        float startY=0;
        final float curveRadius=background.getHeight()/3;

        final float offsetX=endX-(actionButton.getX()+actionButton.getWidth()/2);
        final float offsetY=endY-(actionButton.getY()+actionButton.getHeight()/2);

        endX=offsetX;
        endY=offsetY;

        Path arcPath = new Path();

        float midX = startX + ((endX - startX) / 2);
        float midY = startY + ((endY - startY) / 2);
        float xDiff = midX - startX;
        float yDiff = midY - startY;

        double angle = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);

        float pointX = (float) (midX + curveRadius * Math.cos(angleRadians));
        float pointY = (float) (midY + curveRadius * Math.sin(angleRadians));

        arcPath.moveTo(0, 0);
        arcPath.cubicTo(0,0,pointX,pointY, endX, endY);

        ValueAnimator pathAnimator=ValueAnimator.ofFloat(0,1);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float point[]=new float[2];
            private volatile boolean isFired;
            private PathMeasure pathMeasure = new PathMeasure(arcPath, false);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value=animation.getAnimatedFraction();
                // Gets the point at the fractional path length
                pathMeasure.getPosTan(pathMeasure.getLength() * value, point, null);

                // Sets view location to the above point
                actionButton.setTranslationX(point[0]);
                actionButton.setTranslationY(point[1]);

                if(!isFired){
                    if(animation.getAnimatedFraction()>=0.35){
                        isFired=true;
                        setUpReveal();
                        runButtonAnimation();
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
        pathAnimator.setInterpolator(new DecelerateInterpolator());
        pathAnimator.setDuration(300);pathAnimator.start();
    }

    private void runRevealNProgress(){
        revealAnimator.setDuration(455);
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
                                .setDuration(150)
                                .setListener(null).start();
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

    private Path createArcPath(View view, float endX, float endY,float radius){
        Path arcPath=new Path();
        float startX=view.getTranslationX();
        float startY=view.getTranslationY();
        float midX = startX + ((endX - startX) / 2);
        float midY = startY + ((endY - startY) / 2);
        float xDiff = midX - startX;
        float yDiff = midY - startY;

        double angle = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);

        float pointX = (float) (midX + radius * Math.cos(angleRadians));
        float pointY = (float) (midY + radius * Math.sin(angleRadians));

        arcPath.moveTo(startX, startY);
        arcPath.cubicTo(startX,startY,pointX,pointY, endX, endY);
        return arcPath;
    }

    private void backAnimation(){
        float endX=0;
        float endY=0;
        float startX=actionButton.getTranslationX();
        float startY=actionButton.getTranslationY();
        final float curveRadius=-background.getHeight()/2;

        Path arcPath = new Path();

        float midX = startX + ((endX - startX) / 2);
        float midY = startY + ((endY - startY) / 2);
        float xDiff = midX - startX;
        float yDiff = midY - startY;

        double angle = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);

        float pointX = (float) (midX + curveRadius * Math.cos(angleRadians));
        float pointY = (float) (midY + curveRadius * Math.sin(angleRadians));

        arcPath.moveTo(startX, startY);
        arcPath.cubicTo(startX,startY,pointX,pointY, endX, endY);

        ValueAnimator pathAnimator=ValueAnimator.ofFloat(0,1);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float point[]=new float[2];
            private PathMeasure pathMeasure = new PathMeasure(arcPath, false);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value=animation.getAnimatedFraction();
                // Gets the point at the fractional path length
                pathMeasure.getPosTan(pathMeasure.getLength() * value, point, null);

                // Sets view location to the above point
                actionButton.setTranslationX(point[0]);
                actionButton.setTranslationY(point[1]);
            }
        });
        pathAnimator.setInterpolator(new DecelerateInterpolator());
        pathAnimator.setDuration(300);pathAnimator.start();
    }
}

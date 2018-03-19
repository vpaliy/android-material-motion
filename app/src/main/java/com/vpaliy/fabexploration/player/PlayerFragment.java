package com.vpaliy.fabexploration.player;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

import com.vpaliy.fabexploration.BaseFragment;
import com.vpaliy.fabexploration.R;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

public class PlayerFragment extends BaseFragment {

    @BindView(R.id.fab)
    protected FloatingActionButton actionButton;

    @BindView(R.id.parent)
    protected ViewGroup parent;

    @BindView(R.id.background)
    protected View background;

    @BindView(R.id.controls_panel)
    protected ViewGroup panel;

    @BindViews({R.id.album, R.id.track_author})
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
    protected int mainRes() {
        return R.layout.fragment_player;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            setUpActionBar();
            setUpPanel();
            setUpButton();
        }
    }

    private void setUpButton() {
        divider.post(() -> {
            float offsetY = divider.getY() - (actionButton.getY() + actionButton.getHeight() / 2);
            actionButton.setTranslationY(offsetY);
        });
    }


    private void setUpPanel() {
        panel.post(() -> {
            panel.setOnClickListener(v -> {
                final int w = panel.getWidth();
                final int h = panel.getHeight();
                final int endRadius = (int) Math.hypot(w, h);
                final float offsetY = (actionButton.getY() + actionButton.getHeight() / 2) - divider.getTop();
                final int cx = (int) (actionButton.getX() + actionButton.getWidth() / 2);
                final int cy = (int) (offsetY);
                setUpPlayDrawable();
                revealAnimator = ViewAnimationUtils.createCircularReveal(panel, cx, cy, endRadius, actionButton.getHeight() / 2);
                revealAnimator.removeAllListeners();
                revealAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        ViewCompat.setElevation(actionButton, 0);
                        fadeInOutViews(1, duration(R.integer.fade_duration));
                        actionButton.setVisibility(View.VISIBLE);
                        actionButton.animate()
                                .alpha(1)
                                .setDuration(duration(R.integer.fade_in_duration))
                                .setListener(null)
                                .start();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        panel.setVisibility(View.GONE);
                        backAnimation();
                        divider.animate()
                                .setDuration(duration(R.integer.fade_in_duration))
                                .scaleY(1).start();
                        bottomBackground.setPivotY(0);
                        bottomBackground.animate()
                                .setDuration(duration(R.integer.fade_in_duration))
                                .scaleY(0).start();
                        runIconScale(0, R.drawable.ic_volume_bottom,
                                ContextCompat.getColor(getContext(), R.color.color_grey));
                        setUpReveal();
                    }
                });
                revealAnimator.setDuration(duration(R.integer.fade_in_duration));
                revealAnimator.start();
            });
            panel.setVisibility(View.GONE);
        });
    }

    private void setUpActionBar() {
        actionBar.inflateMenu(R.menu.main);
    }

    private void setUpReveal() {
        int w = panel.getWidth();
        int h = panel.getHeight();
        final int endRadius = (int) Math.hypot(w, h);
        final int cx = (int) (actionButton.getX() + actionButton.getWidth() / 2);
        final int cy = (int) (actionButton.getY() + actionButton.getHeight() / 2 - background.getTop());

        final float deltaX = cx - (playPause.getLeft() + playPause.getWidth() / 2);
        final float deltaY = (cy - getResources().getDimension(R.dimen.play_pause_size) / 2) - (playPause.getTop());
        playPause.setTranslationX(deltaX);
        playPause.setTranslationY(deltaY);
        revealAnimator = ViewAnimationUtils.createCircularReveal(panel, cx, cy, actionButton.getHeight(), endRadius);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                panel.setVisibility(View.VISIBLE);
                actionButton.setVisibility(View.INVISIBLE);
                fadeInOutViews(0, duration(R.integer.fade_in_duration));
            }
        });
        revealAnimator.setDuration(duration(R.integer.conceal_duration) / 2);
        revealAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    private void runButtonAnimation() {
        next.setScaleX(0);
        next.setScaleY(0);
        prev.setScaleX(0);
        prev.setScaleY(0);
        Path arcPath = createArcPath(playPause, 0, 0, -playPause.getTranslationY());
        ValueAnimator pathAnimator = ValueAnimator.ofFloat(0, 1);
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
        pathAnimator.setDuration(duration(R.integer.path_duration) / 2);
        pathAnimator.start();
        next.animate()
                .setDuration(duration(R.integer.scale_duration))
                .setStartDelay(duration(R.integer.short_delay))
                .scaleX(1).scaleY(1)
                .start();
        prev.animate()
                .setDuration(duration(R.integer.scale_duration))
                .setStartDelay(duration(R.integer.short_delay))
                .scaleX(1).scaleY(1)
                .start();
    }

    private void setUpPauseDrawable() {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_pause);
        actionButton.setImageDrawable(drawable);
        playPause.setImageDrawable(drawable);
    }

    private void setUpPlayDrawable() {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_play);
        actionButton.setImageDrawable(drawable);
        playPause.setImageDrawable(drawable);
    }

    @OnClick(R.id.fab)
    public void onButtonClick() {
        setUpPauseDrawable();
        final float playPauseY = playPause.getY() + background.getY();
        float endX = background.getWidth() / 2;
        float endY = playPauseY + playPause.getHeight() / 2;
        float startX = 0;
        float startY = 0;
        final float curveRadius = background.getHeight() / 3;

        final float offsetX = endX - (actionButton.getX() + actionButton.getWidth() / 2);
        final float offsetY = endY - (actionButton.getY() + actionButton.getHeight() / 2);

        endX = offsetX;
        endY = offsetY;

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
        arcPath.cubicTo(0, 0, pointX, pointY, endX, endY);

        ValueAnimator pathAnimator = ValueAnimator.ofFloat(0, 1);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float point[] = new float[2];
            private volatile boolean isFired;
            private PathMeasure pathMeasure = new PathMeasure(arcPath, false);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value = animation.getAnimatedFraction();
                // Gets the point at the fractional path length
                pathMeasure.getPosTan(pathMeasure.getLength() * value, point, null);

                // Sets view location to the above point
                actionButton.setTranslationX(point[0]);
                actionButton.setTranslationY(point[1]);

                if (!isFired) {
                    if (animation.getAnimatedFraction() >= 0.35) {
                        isFired = true;
                        setUpReveal();
                        runButtonAnimation();
                        //reveal and animate the thumb
                        runRevealNProgress();
                        //stretch out the top divider
                        runTopDividerScale();
                        //expand the bottom divider
                        runBottomDividerScale();
                        //scale icon, swap icon, scale icon
                        runIconScale(0, R.drawable.ic_play_bottom, Color.WHITE);
                    }
                }
            }
        });
        pathAnimator.setInterpolator(new DecelerateInterpolator());
        pathAnimator.setDuration(duration(R.integer.path_duration));
        pathAnimator.start();
    }

    private void runRevealNProgress() {
        revealAnimator.setDuration(duration(R.integer.conceal_duration));
        revealAnimator.setInterpolator(new DecelerateInterpolator());
        seekBar.setProgress(80);
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(seekBar, "progress", 80, 20);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(seekBar, View.SCALE_Y, 0, 1f);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.setDuration(duration(R.integer.progress_duration));
        scaleY.setDuration(duration(R.integer.progress_duration));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(revealAnimator);
        animatorSet.play(progressAnimator).with(scaleY);
        animatorSet.start();
    }

    private void fadeInOutViews(int alpha, int duration) {
        for (final View view : fadeViews) {
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

    private void runIconScale(int delay, @DrawableRes int drawable, int color) {
        soundPlay.animate()
                .scaleY(0)
                .scaleX(0)
                .setDuration(duration(R.integer.short_delay))
                .setStartDelay(delay)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        trackTitle.setTextColor(color);
                        soundPlay.setImageDrawable(ContextCompat.getDrawable(getContext(), drawable));
                        soundPlay.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(duration(R.integer.scale_duration))
                                .setListener(null).start();
                    }
                }).start();
    }

    private void runBottomDividerScale() {
        bottomBackground.setPivotY(0);
        bottomBackground.animate()
                .setDuration(duration(R.integer.divider_duration))
                .scaleY(100).start();
    }

    private void runTopDividerScale() {
        divider.animate()
                .setDuration(duration(R.integer.divider_duration))
                .scaleY(30).start();
    }


    private void backAnimation() {
        float endX = 0;
        float endY = 0;
        float startX = actionButton.getTranslationX();
        float startY = actionButton.getTranslationY();
        final float curveRadius = -background.getHeight() / 2;

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
        arcPath.cubicTo(startX, startY, pointX, pointY, endX, endY);

        ValueAnimator pathAnimator = ValueAnimator.ofFloat(0, 1);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float point[] = new float[2];
            private PathMeasure pathMeasure = new PathMeasure(arcPath, false);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value = animation.getAnimatedFraction();
                // Gets the point at the fractional path length
                pathMeasure.getPosTan(pathMeasure.getLength() * value, point, null);

                // Sets view location to the above point
                actionButton.setTranslationX(point[0]);
                actionButton.setTranslationY(point[1]);
            }
        });
        pathAnimator.setInterpolator(new DecelerateInterpolator());
        pathAnimator.setDuration(duration(R.integer.path_duration));
        pathAnimator.start();
    }
}
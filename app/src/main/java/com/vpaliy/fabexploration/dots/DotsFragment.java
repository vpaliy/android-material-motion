package com.vpaliy.fabexploration.dots;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import com.vpaliy.fabexploration.BaseFragment;
import com.vpaliy.fabexploration.R;
import java.util.List;
import io.codetail.animation.ViewAnimationUtils;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import android.support.annotation.Nullable;

public class DotsFragment extends BaseFragment {

    @BindViews({R.id.first,R.id.second,R.id.third})
    protected List<FloatingActionButton> dots;

    @BindView(R.id.parent)
    protected ViewGroup parent;

    @BindView(R.id.topPanel)
    protected View topPanel;

    @BindView(R.id.top)
    protected ViewGroup top;

    @BindView(R.id.close)
    protected ImageView close;

    private FloatingActionButton lastDot;

    private boolean isFolded;
    private boolean finished=true;

    @Override
    protected int mainRes() {
        return R.layout.fragment_dots;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            topPanel.post(()->topPanel.setVisibility(View.GONE));
            close.post(()->close.setVisibility(View.GONE));
        }
    }

    @OnClick(R.id.first)
    public void revealFirst(FloatingActionButton dot) {
        if (finished) {
            finished=false;
            lastDot = dot;
            float deltaX = topPanel.getWidth() / 2 - dot.getX() - dot.getWidth() / 2;
            float deltaY = topPanel.getHeight() / 2 - dot.getY() - dot.getHeight() / 2;
            deltaY -= topPanel.getHeight() / 2 + getResources().getDimension(R.dimen.morph_radius) / 4;
            Path arcPath = createArcPath(dot, deltaX, deltaY, -deltaX);
            ValueAnimator pathAnimator = ValueAnimator.ofFloat(0, 1);
            pathAnimator.addUpdateListener(new ArcListener(arcPath, dot));
            topPanel.setBackgroundColor(dot.getBackgroundTintList().getDefaultColor());
            pathAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Animator animator = createRevealAnimator(dot, 0);
                    animator.start();
                    isFolded = !isFolded;
                    finished=true;

                }
            });
            AnimatorSet animatorSet = morphParent();
            animatorSet.play(pathAnimator);
            addScaleAnimation(50, 150, animatorSet);
            animatorSet.start();
        }
    }

    @OnClick(R.id.topPanel)
    public void conceal(){
        if(!finished) return;
        finished=false;
        if(lastDot.getId()==R.id.second){
            int height=top.getHeight();
            ValueAnimator heightAnimation = ValueAnimator.ofInt(top.getHeight(),parent.getHeight());
            heightAnimation.addUpdateListener(valueAnimator-> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = top.getLayoutParams();
                layoutParams.height = val;
                top.setLayoutParams(layoutParams);
            });
            heightAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Animator animator=createRevealAnimator(lastDot,-lastDot.getHeight());
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            AnimatorSet animatorSet=morphParent();
                            addScaleAnimation(50,150,animatorSet);
                            animatorSet.start();
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            lastDot.setVisibility(View.VISIBLE);
                            topPanel.setVisibility(View.GONE);
                            top.getLayoutParams().height=height;
                            isFolded=!isFolded;
                            finished=true;
                        }
                    });
                    animator.start();
                }
            });
            heightAnimation.setDuration(200);
            heightAnimation.start();
            return;
        }

        Animator animator=createRevealAnimator(lastDot,0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                lastDot.setVisibility(View.VISIBLE);
                topPanel.setVisibility(View.GONE);
                Path arcPath=createArcPath(lastDot,0,0,lastDot.getTranslationX());
                ValueAnimator pathAnimator=ValueAnimator.ofFloat(0,1);
                pathAnimator.addUpdateListener(new ArcListener(arcPath,lastDot));
                AnimatorSet animatorSet=morphParent();
                animatorSet.play(pathAnimator);
                addScaleAnimation(50,150,animatorSet);
                animatorSet.start();
                isFolded=!isFolded;
                finished=true;
            }
        });
        animator.start();
    }

    @OnClick(R.id.second)
    public void revealThird(FloatingActionButton dot){
        if(!finished) return;
        lastDot=dot;
        ViewGroup.LayoutParams params=top.getLayoutParams();
        int height=params.height;
        params.height= ConstraintLayout.LayoutParams.MATCH_PARENT;
        top.setLayoutParams(params);
        topPanel.setBackgroundColor(dot.getBackgroundTintList().getDefaultColor());
        top.post(()->{
            Animator animator=createRevealAnimator(lastDot,0);
            AnimatorSet animatorSet=morphParent();
            animatorSet.play(animator);
            addScaleAnimation(0,100,animatorSet);
            animatorSet.start();
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ValueAnimator heightAnimation = ValueAnimator.ofInt(top.getHeight(),height);
                    heightAnimation.addUpdateListener(valueAnimator-> {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = top.getLayoutParams();
                        layoutParams.height = val;
                        top.setLayoutParams(layoutParams);
                    });
                    heightAnimation.setDuration(200);
                    heightAnimation.start();
                    isFolded=!isFolded;
                    finished=true;
                }
            });
        });
    }

    private Animator createRevealAnimator(FloatingActionButton dot, float offsetY){
        ViewCompat.setElevation(dot,0);
        dot.setVisibility(View.INVISIBLE);
        lastDot=dot;
        int cx=(int)(dot.getX()+dot.getHeight()/2);
        int cy=(int)(dot.getY()+dot.getHeight()/2+offsetY);
        int w = topPanel.getWidth();
        int h = topPanel.getHeight();
        final int endRadius = !isFolded?(int) Math.hypot(w, h):dot.getHeight()/2;
        final int startRadius=isFolded?(int) Math.hypot(w, h):dot.getHeight()/2;
        topPanel.setVisibility(View.VISIBLE);
        Animator animator= ViewAnimationUtils.createCircularReveal(topPanel,cx,cy,startRadius,endRadius);
        animator.setDuration(400);
        return animator;
    }

    private void addScaleAnimation(int startDelay, int duration, AnimatorSet set){
        final int start=!isFolded?1:0;
        final int end =~start&0x1;
        AnimatorSet buttonSet=new AnimatorSet();
        for(int index=0;index<dots.size();index++){
            FloatingActionButton tempDot=dots.get(index);
            if(tempDot.getId()!=lastDot.getId()){
                int delay=index*startDelay;
                ObjectAnimator scaleX=ObjectAnimator.ofFloat(tempDot,View.SCALE_X,start,end);
                ObjectAnimator scaleY=ObjectAnimator.ofFloat(tempDot,View.SCALE_Y,start,end);
                ObjectAnimator fade=ObjectAnimator.ofFloat(tempDot,View.ALPHA,start,end);
                scaleX.setStartDelay(delay);
                scaleY.setStartDelay(delay);
                fade.setStartDelay(delay);
                buttonSet.playTogether(scaleX,scaleY,fade);
            }
        }
        buttonSet.setDuration(duration);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                buttonSet.start();
            }
        });
    }

    private Path createArcPath(View view, float endX, float endY, float radius){
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

    private class ArcListener implements ValueAnimator.AnimatorUpdateListener{

        private float point[]=new float[2];
        private PathMeasure pathMeasure;
        private View dot;

        public ArcListener(Path path, View dot){
            this.pathMeasure = new PathMeasure(path, false);
            this.dot=dot;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            final float value=animation.getAnimatedFraction();
            // Gets the point at the fractional path length
            pathMeasure.getPosTan(pathMeasure.getLength() * value, point, null);

            // Sets view location to the above point
            dot.setTranslationX(point[0]);
            dot.setTranslationY(point[1]);
        }
    }

    private AnimatorSet morphParent(){
        GradientDrawable drawable=GradientDrawable.class.cast(parent.getBackground());
        int endValue=isFolded?getResources().getDimensionPixelOffset(R.dimen.morph_radius):0;
        ObjectAnimator cornerAnimation = ObjectAnimator.ofFloat(drawable, "cornerRadius", endValue);
        endValue=isFolded?parent.getHeight()/2:parent.getHeight()*2;
        ValueAnimator heightAnimation = ValueAnimator.ofInt(parent.getHeight(),endValue);
        heightAnimation.addUpdateListener(valueAnimator-> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
            layoutParams.height = val;
            parent.setLayoutParams(layoutParams);
        });

        AnimatorSet set=new AnimatorSet();
        set.setInterpolator(new AccelerateInterpolator());
        set.playTogether(cornerAnimation,heightAnimation);
        set.setDuration(500);
        return set;
    }
}
package com.vpaliy.fabexploration;

import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private Unbinder unbinder;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(unbinder!=null){
            unbinder.unbind();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(mainRes(),container,false);
        setRetainInstance(true);
        unbinder= ButterKnife.bind(this,root);
        return root;
    }

    @LayoutRes
    protected abstract int mainRes();

    protected Path createArcPath(View view, float endX, float endY, float radius){
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

    protected int duration(@IntegerRes int resource){
        return getResources().getInteger(resource);
    }

    protected class ArcListener implements ValueAnimator.AnimatorUpdateListener{

        private float point[]=new float[2];
        private PathMeasure pathMeasure;
        private View target;

        public ArcListener(Path path, View target){
            this.pathMeasure = new PathMeasure(path, false);
            this.target=target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            final float value=animation.getAnimatedFraction();
            // Gets the point at the fractional path length
            pathMeasure.getPosTan(pathMeasure.getLength() * value, point, null);

            // Sets view location to the above point
            target.setTranslationX(point[0]);
            target.setTranslationY(point[1]);
        }
    }

}

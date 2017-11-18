package com.vpaliy.fabexploration.share

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import com.vpaliy.fabexploration.*
import kotlinx.android.synthetic.main.fragment_share.*


class ShareFragment: BaseFragment(){
    override fun mainRes()= R.layout.fragment_share

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        share.click {
            val deltaX=root.halfWidth() - share.x - share.halfWidth()
            val deltaY=root.halfHeight() - share.y - share.halfHeight()
            val path=createArcPath(share,deltaX,deltaY,-deltaX)
            ValueAnimator.ofFloat(0f,1f).apply {
                addUpdateListener(ArcListener(path,share))
                addListener(object:AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        //reveal animator goes here
                    }
                })
            }.start()
        }
    }
}
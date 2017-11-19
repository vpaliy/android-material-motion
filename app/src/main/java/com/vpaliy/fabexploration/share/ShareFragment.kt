package com.vpaliy.fabexploration.share

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import com.vpaliy.fabexploration.*
import com.vpaliy.kotlin_extensions.animator
import com.vpaliy.kotlin_extensions.hide
import com.vpaliy.kotlin_extensions.scale
import com.vpaliy.kotlin_extensions.show
import io.codetail.animation.ViewAnimationUtils
import kotlinx.android.synthetic.main.fragment_share.*


class ShareFragment: BaseFragment(){
    private var isRevealed=false
    private val items by lazy(LazyThreadSafetyMode.NONE){
        mutableListOf(git,facebook,linkedIn,twitter)
    }

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
                        reveal()
                    }
                })
            }.start()
        }
    }

    private fun reveal(){
        val cx = share.x+share.halfWidth()
        val cy = share.y+share.halfHeight()
        val endRadius =Math.hypot(root.width.toDouble(), root.height.toDouble()).toFloat()
        val startRadius = share.halfHeight()
        share.hide(false)
        background.show()
        ViewAnimationUtils.createCircularReveal(background, cx.toInt(), cy.toInt(), startRadius, endRadius).apply {
            duration=300
            addListener(object:AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    items.forEachIndexed{index,item->
                        item.animator {
                            scale(1f)
                            duration=300L
                            startDelay=index * 50L
                        }.start()
                    }
                    isRevealed=!isRevealed
                }
            })
        }.start()
    }
}
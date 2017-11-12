package com.vpaliy.fabexploration.sheets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import com.vpaliy.fabexploration.getHeightAnimator

class SheetAnimator:DefaultItemAnimator(){
    private val animatorMap= mutableMapOf<RecyclerView.ViewHolder,ValueAnimator>()

    override fun animateChange(oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder,
                               preInfo: ItemHolderInfo, postInfo: ItemHolderInfo): Boolean {
        if(preInfo is SheetInfo){
            animatorMap[newHolder]?.cancel()
            val animator=newHolder.itemView.getHeightAnimator(preInfo.height)
            animatorMap[newHolder]=animator
            animator.addListener(object:AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    animatorMap.remove(newHolder)
                    dispatchAnimationFinished(newHolder)
                }
            })
            animator.start()
        }
        return false
    }

    override fun endAnimation(item: RecyclerView.ViewHolder?) {
        super.endAnimation(item)
        animatorMap[item]?.cancel()
    }

    override fun endAnimations() {
        super.endAnimations()
        animatorMap.forEach { it.value.cancel() }
    }

    override fun recordPreLayoutInformation(state: RecyclerView.State, viewHolder: RecyclerView.ViewHolder,
                                            changeFlags: Int, payloads: MutableList<Any>): ItemHolderInfo {
        if(changeFlags== RecyclerView.ItemAnimator.FLAG_CHANGED){
            payloads.forEach {
                if(it is Int) {
                    return SheetInfo(it)
                }
            }
        }
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder)=true

    inner class SheetInfo(val height:Int):ItemHolderInfo()
}
package com.vpaliy.fabexploration

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.support.annotation.DimenRes
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.util.Log
import android.view.View
import android.view.animation.Animation

fun View.getHeightAnimator(endHeight:Int, duration:Long=200):ValueAnimator{
    val heightAnimator = ValueAnimator.ofInt(height, endHeight)
    heightAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        layoutParams.height =value
        requestLayout()
    }
    heightAnimator.duration=duration
    return heightAnimator
}

fun View.getWidthAnimator(endWidth:Int, duration:Long=200):ValueAnimator{
    val widthAnimator = ValueAnimator.ofInt(width, endWidth)
    widthAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        layoutParams.width =value
        requestLayout()
    }
    widthAnimator.duration=duration
    return widthAnimator
}

fun View.setHeight(endHeight: Int){
    layoutParams.height=endHeight
    requestLayout()
}

fun AnimatorSet.playWith(vararg items: Animator)=apply {
    playTogether(items.toMutableList())
}

fun Animator.playWith(animator: Animator): Animator {
    if(animator is AnimatorSet)
        return animator.playWith(this)
    else if(this is AnimatorSet)
        return playWith(animator)
    return AnimatorSet().playWith(this,animator)
}

fun Fragment.getDimension(@DimenRes id:Int)=resources.getDimension(id)

infix fun View.setElevation(@DimenRes id:Int){
    ViewCompat.setElevation(this,context.getDimensFloat(id))
}

fun Any.log(message:Any,tag:String=this.javaClass.name){
    Log.d(tag,message.toString())
}

fun View.click(function:()->Unit)=setOnClickListener { function() }

fun View.halfWidth()=width/2f

fun View.halfHeight()=height/2f

infix fun <T> Boolean.then(value:T)=if(this) value else null

infix fun Context.getDimens(id:Int)=resources.getDimensionPixelOffset(id)

infix fun Context.getDimensFloat(id:Int)=resources.getDimension(id)

fun Animator.onEnd(callback:(Animator?)->Unit)=Keeper(this).onEnd(callback)

fun Animator.onStart(callback:(Animator?)->Unit)=Keeper(this).onStart(callback)

fun Animator.onCancel(callback:(Animator?)->Unit)=Keeper(this).onCancel(callback)

fun Animator.onRepeat(callback:(Animator?)->Unit)=Keeper(this).onRepeat(callback)

class Keeper(private val animator:Animator){
    private val executor=ListenerExecutor()

    init { animator.addListener(executor) }

    fun onEnd(callback:(Animator?) -> Unit)=apply {
        executor.end=callback
    }

    fun onStart(callback: (Animator?) -> Unit)=apply {
        executor.start=callback
    }

    fun onCancel(callback: (Animator?) -> Unit)=apply {
        executor.cancel=callback
    }

    fun onRepeat(callback: (Animator?) -> Unit)=apply {
        executor.repeat=callback
    }

    fun animator()=animator
}


private class ListenerExecutor:Animator.AnimatorListener {
    var end:((Animator?) -> Unit)? = null
    var start: ((Animator?) ->Unit)? = null
    var cancel:((Animator?)->Unit)?=null
    var repeat:((Animator?)->Unit)?=null

    override fun onAnimationCancel(animation: Animator?) {
        cancel?.invoke(animation)
    }

    override fun onAnimationEnd(animation: Animator?) {
        end?.invoke(animation)
    }

    override fun onAnimationRepeat(animation: Animator?) {
        repeat?.invoke(animation)
    }

    override fun onAnimationStart(animation: Animator?) {
        start?.invoke(animation)
    }
}


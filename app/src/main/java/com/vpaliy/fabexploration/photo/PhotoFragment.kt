package com.vpaliy.fabexploration.photo

import android.animation.AnimatorSet
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vpaliy.fabexploration.*
import com.vpaliy.kotlin_extensions.getHeightAnimator
import com.vpaliy.kotlin_extensions.info
import com.vpaliy.kotlin_extensions.then
import kotlinx.android.synthetic.main.fragment_photo.view.*

class PhotoFragment:Fragment(){

    private val size by lazy(LazyThreadSafetyMode.NONE) {
        getDimension(R.dimen.photo_size).toInt()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View?
                =inflater.inflate(R.layout.fragment_photo,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view){
            val startHeight=card.height
            val startWidth=card.width
            info(this@PhotoFragment,startWidth).info(this@PhotoFragment,startHeight)
            card.click{
                val height=(card.height!=startHeight) then size?:startHeight
                val heightAnimator=card.getHeightAnimator(height)
                val width=(card.width!=startWidth) then size?:startWidth
                val widthAnimator=card.getWidthAnimator(width)
                AnimatorSet().playWith(heightAnimator,widthAnimator).apply {
                    duration=300
                }.start()
            }
        }
    }
}
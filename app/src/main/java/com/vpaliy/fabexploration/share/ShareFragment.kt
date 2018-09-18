package com.vpaliy.fabexploration.share

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.vpaliy.fabexploration.*
import com.vpaliy.kotlin_extensions.animator
import com.vpaliy.kotlin_extensions.hide
import com.vpaliy.kotlin_extensions.scale
import com.vpaliy.kotlin_extensions.show
import io.codetail.animation.ViewAnimationUtils
import kotlinx.android.synthetic.main.fragment_share.*

class ShareFragment : BaseFragment() {
  private val items by lazy(LazyThreadSafetyMode.NONE) {
    mutableListOf(git, facebook, linkedIn, twitter)
  }

  override fun mainRes() = R.layout.fragment_share

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    share.click {
      val deltaX = root.halfWidth() - share.x - share.halfWidth()
      val deltaY = root.halfHeight() - share.y - share.halfHeight()
      val path = createArcPath(share, deltaX, deltaY, -deltaX)
      ValueAnimator.ofFloat(0f, 1f).apply {
        addUpdateListener(ArcListener(path, share))
        onEnd { reveal() }
      }.start()
    }

    background.click { conceal() }
  }

  private fun reveal() {
    val cx = share.x + share.halfWidth()
    val cy = share.y + share.halfHeight()
    val endRadius = Math.hypot(root.width.toDouble(), root.height.toDouble()).toFloat()
    val startRadius = share.height.toFloat()
    share.hide(false)
    background.show()
    ViewAnimationUtils.createCircularReveal(background, cx.toInt(), cy.toInt(), startRadius, endRadius).apply {
      duration = 850
      interpolator = DecelerateInterpolator()
      onStart {
        items.forEachIndexed { index, item ->
          item.animator {
            scale(1f)
            duration = 150L
            startDelay = maxOf(index, 1) * 90L
          }.start()
        }
      }
    }.start()
  }

  private fun conceal() {
    val cx = share.x + share.halfWidth()
    val cy = share.y + share.halfHeight()
    val startRadius = Math.hypot(root.width.toDouble(), root.height.toDouble()).toFloat()
    val endRadius = share.height.toFloat()
    ViewAnimationUtils.createCircularReveal(background, cx.toInt(), cy.toInt(), startRadius, endRadius).onStart {
      items.forEachIndexed { index, item ->
        item.animator {
          scale(0f)
          duration = 300L
          startDelay = index * 50L
        }.start()
      }
    }.onEnd {
      background.hide(false)
      share.show()
      adjustButton()
    }.animator().apply {
      duration = 500
      interpolator = AccelerateInterpolator()
    }.start()
  }

  private fun adjustButton() {
    val path = createArcPath(share, 0f, 0f, share.translationX)
    ValueAnimator.ofFloat(0f, 1f).apply {
      startDelay = 300L
      addUpdateListener(ArcListener(path, share))
    }.start()
  }
}
package com.vpaliy.fabexploration.sheets

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vpaliy.fabexploration.*

class SheetAdapter(context: Context) : RecyclerView.Adapter<SheetAdapter.SheetViewHolder>() {
  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private val expandedHeight = context.getDimens(R.dimen.sheet_expanded_height)
  private val shrunkHeight = context.getDimens(R.dimen.sheet_shrunk_height)
  private val heights = IntArray(itemCount, { shrunkHeight })

  inner class SheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    init {
      itemView.setElevation(R.dimen.sheet_elevation)
      itemView.click {
        val height = (heights[adapterPosition] == shrunkHeight) then expandedHeight ?: shrunkHeight
        itemView.getHeightAnimator(height).start()
        heights[adapterPosition] = height
      }
    }

    fun bind() = itemView.setHeight(heights[adapterPosition])
  }

  override fun getItemCount() = 10

  override fun onBindViewHolder(holder: SheetViewHolder, position: Int) = holder.bind()

  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int)
      : SheetViewHolder
      = SheetViewHolder(inflater.inflate(R.layout.adapter_sheet, parent, false))
}
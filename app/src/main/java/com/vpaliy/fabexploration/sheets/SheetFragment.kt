package com.vpaliy.fabexploration.sheets

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vpaliy.fabexploration.R

class SheetFragment : Fragment() {
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.fragment_sheets, container, false) as RecyclerView
    root.adapter = SheetAdapter(context)
    return root
  }
}
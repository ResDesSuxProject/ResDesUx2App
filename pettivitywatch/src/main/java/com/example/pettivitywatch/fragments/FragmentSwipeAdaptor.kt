package com.example.pettivitywatch.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentSwipeAdaptor constructor(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private lateinit var fragments: List<Fragment>
    public constructor(fragmentActivity: FragmentActivity, fragments: List<Fragment>) : this(fragmentActivity) {
        this.fragments = fragments
    }

    override fun getItemCount(): Int {
        return fragments.count()
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
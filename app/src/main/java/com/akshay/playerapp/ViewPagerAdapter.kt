package com.akshay.playerapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

//class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
//    private val fragmentTitle: ArrayList<String> = ArrayList()
//    private val fragmentArrayList: ArrayList<Fragment> = ArrayList()
//    override fun getCount(): Int {
//        return fragmentArrayList.size
//    }
//    override fun getItem(position: Int): Fragment {
//        return fragmentArrayList[position]
//    }
//    fun addFragment(fragment: Fragment, title: String){
//        fragmentArrayList.add(fragment)
//        fragmentTitle.add(title)
//    }
//    override fun getPageTitle(position: Int): CharSequence? {
//        return fragmentTitle[position]
//    }
//}

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    private val pages: List<Fragment> = listOf(AudioFragment(), VideoFragment(), FolderFragment())
    override fun getCount(): Int {
        return pages.size
    }

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }
}
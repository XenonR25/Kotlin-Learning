package com.example.assignment1

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.fragment.app.Fragment

class FoodDetailPagerAdapter(
    activity: AppCompatActivity,
    private val food: Food
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FoodDetailFragment.newInstance(food)
            1 -> FoodReviewFragment.newInstance(food)
            else -> throw IllegalStateException("Invalid position")
        }
    }
}

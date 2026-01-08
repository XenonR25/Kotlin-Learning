package com.example.assignment1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.assignment1.databinding.ActivityFoodDetailBinding
import com.google.android.material.tabs.TabLayoutMediator

class FoodDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Toolbar setup
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        //Change the color of the back button
        binding.toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))

        // Load the fragment
        val food = intent.getSerializableExtra("food_item") as? Food ?: return

        //View page Adapter
        val adapter = FoodDetailPagerAdapter(this,food)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Details"
                1 -> "Reviews"
                else -> ""
            }
        }.attach()

        // Optional: Set title from food
        binding.toolbar.title = food.name
    }
}


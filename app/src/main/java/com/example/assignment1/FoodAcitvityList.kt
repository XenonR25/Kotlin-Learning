package com.example.assignment1

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment1.databinding.ActivityFoodListBinding
import com.example.assignment1.viewModel.FoodViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FoodActivityList : AppCompatActivity(), FoodClickListener {

    private lateinit var adapter: FoodAdapter
    private lateinit var binding: ActivityFoodListBinding
    private val viewModel: FoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityFoodListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // RecyclerView setup
        adapter = FoodAdapter(emptyList(), this)
        binding.foodRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.foodRecyclerView.adapter = adapter

        subscribe()

        binding.showFoodButton.setOnClickListener {
            viewModel.toggle()
        }


    }


    private fun subscribe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    combine(
                        viewModel.buttonText,
                        viewModel.foodList
                    ){ text , foods -> text to foods}
                        .collect{ (text,foods)->
                        binding.showFoodButton.text = text
                        binding.foodRecyclerView.visibility =
                            if(foods.isEmpty() ) View.GONE else View.VISIBLE
                            adapter.updateList(foods)
                    }
                }
            }
        }
    }


    // Click on food image
    override fun onImageClick(food: Food) {
        // Create ImageView programmatically
        Toast.makeText(this, "Image clicked: ${food.name}", Toast.LENGTH_SHORT).show()
        val imageView = ImageView(this).apply {
            setImageResource(food.imageRes)
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(android.graphics.Color.WHITE) // semi-transparent dark background
            setPadding(32, 32, 32, 32)
        }

        // Create AlertDialog
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(imageView)
            .create()

        // Dismiss dialog on click anywhere (optional)
        imageView.setOnClickListener {
            dialog.dismiss()
        }

        // Show dialog
        dialog.show()

    }

        // Click on "Add" button
    override fun onOtherClick(food: Food) {
        Toast.makeText(this, "Added ${food.name} ($${food.price})", Toast.LENGTH_SHORT).show()
    }
}

package com.example.assignment1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.databinding.ItemFoodBinding

class FoodAdapter(
    private var foodList: List<Food>,
    private val listener: FoodClickListener
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFoodBinding.inflate(inflater, parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        val binding = holder.binding

        // Set food data
        binding.foodName.text = food.name
        binding.foodDescription.text = food.description
        binding.foodPrice.text = "$${food.price}"
        binding.foodImage.setImageResource(food.imageRes)

        // Click listeners
        binding.foodImage.setOnClickListener { listener.onImageClick(food) }
        binding.addButton.setOnClickListener { listener.onOtherClick(food)
            val context = binding.root.context
            val intent = android.content.Intent(context, FoodDetailActivity::class.java)
            intent.putExtra("food_item", food) // Make Food implement Serializable
            context.startActivity(intent)}

    }

    override fun getItemCount(): Int = foodList.size

    fun updateList(newList: List<Food>) {
        foodList = newList
        notifyDataSetChanged()
    }
}

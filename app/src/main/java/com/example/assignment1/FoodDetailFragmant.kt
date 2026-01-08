package com.example.assignment1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.assignment1.databinding.FragmentFoodDetailBinding
import androidx.fragment.app.viewModels
import com.example.assignment1.viewModel.FoodDetailsViewModel

class FoodDetailFragment : Fragment() {

    private val viewModel : FoodDetailsViewModel by viewModels()
    private var _binding: FragmentFoodDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_NAME = "food_name"
        private const val ARG_DESC = "food_desc"
        private const val ARG_PRICE = "food_price"
        private const val ARG_IMAGE = "food_image"

        fun newInstance(food: Food): FoodDetailFragment {
            val fragment = FoodDetailFragment()
            val bundle = Bundle()
            bundle.putString(ARG_NAME, food.name)
            bundle.putString(ARG_DESC, food.description)
            bundle.putDouble(ARG_PRICE, food.price)
            bundle.putInt(ARG_IMAGE, food.imageRes)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve arguments
        val name = arguments?.getString(ARG_NAME)
        val desc = arguments?.getString(ARG_DESC)
        val price = arguments?.getDouble(ARG_PRICE, 0.0)
        val imageRes = arguments?.getInt(ARG_IMAGE, 0)



        // Set food data
        binding.foodName.text = name
        binding.foodDescription.text = desc
        binding.foodPrice.text = "$$price"
        imageRes?.let { binding.foodImage.setImageResource(it) }

        viewModel.isFavourite.observe(viewLifecycleOwner) {
            isFav ->
            if(isFav) {
                binding.favouriteIcon.setImageResource(R.drawable.favourite)
            } else {
                binding.favouriteIcon.setImageResource(R.drawable.favourite_checked)
            }
        }

        binding.favouriteIcon.setOnClickListener {
            viewModel.toggleFavourite()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

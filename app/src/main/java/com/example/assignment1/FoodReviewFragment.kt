package com.example.assignment1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment1.databinding.FragmentFoodReviewBinding

class FoodReviewFragment : Fragment() {

    private var _binding: FragmentFoodReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var food: Food

    companion object {
        private const val ARG_FOOD = "food"

        fun newInstance(food: Food): FoodReviewFragment {
            val fragment = FoodReviewFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_FOOD, food)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        food = arguments?.getSerializable(ARG_FOOD) as Food
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodReviewBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sample review list
        val reviews = listOf(
            Review("Alice", 5, "Delicious and fresh!"),
            Review("Bob", 4, "Tasty, but a bit salty."),
            Review("Charlie", 5, "Highly recommend!")
        )

        val adapter = ReviewAdapter(reviews)
        binding.recyclerViewReviews.adapter = adapter
        binding.recyclerViewReviews.layoutManager = LinearLayoutManager(requireContext())
    }
}

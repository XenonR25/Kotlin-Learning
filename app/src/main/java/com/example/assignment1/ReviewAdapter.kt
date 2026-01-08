package com.example.assignment1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.databinding.ItemReviewBinding

class ReviewAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.binding.username.text = review.username
        holder.binding.ratingBar.rating = review.rating.toFloat()
        holder.binding.comment.text = review.comment
    }

    override fun getItemCount(): Int = reviews.size
}

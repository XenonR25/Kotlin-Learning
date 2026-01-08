// ProductAdapter.kt
package com.example.assignment1

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.DataClass.ProductDto
import com.example.assignment1.DataClass.UiItem
import com.example.assignment1.DataClass.UserDto
import com.example.assignment1.databinding.ItemErrorBinding
import com.example.assignment1.databinding.ItemLoadingBinding
import com.example.assignment1.databinding.ItemProductBinding
import com.example.assignment1.databinding.ItemUserBinding

class ProductAdapter(private val onRetryClicked : (() -> Unit)? = null) : ListAdapter<UiItem, RecyclerView.ViewHolder>(DiffCallBack) {

    companion object {
        private const val TYPE_PRODUCT = 1
        private const val TYPE_USER = 2
        private const val TYPE_LOADING = 3
        private const val TYPE_ERROR = 4
    }

    object DiffCallBack : DiffUtil.ItemCallback<UiItem>() {
        override fun areItemsTheSame(oldItem: UiItem, newItem: UiItem): Boolean {
            return when {
                oldItem is UiItem.ProductItem && newItem is UiItem.ProductItem ->
                    oldItem.product.id == newItem.product.id
                oldItem is UiItem.UserItem && newItem is UiItem.UserItem ->
                    oldItem.user.id == newItem.user.id
                oldItem is UiItem.LoadingItem && newItem is UiItem.LoadingItem -> true
                oldItem is UiItem.ErrorItem && newItem is UiItem.ErrorItem ->
                    oldItem.message == newItem.message
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: UiItem, newItem: UiItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiItem.ProductItem -> TYPE_PRODUCT
            is UiItem.UserItem -> TYPE_USER
            is UiItem.LoadingItem -> TYPE_LOADING
            is UiItem.ErrorItem -> TYPE_ERROR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_PRODUCT -> {
                val binding = ItemProductBinding.inflate(inflater, parent, false)
                ProductViewHolder(binding)
            }
            TYPE_USER -> {
                val binding = ItemUserBinding.inflate(inflater, parent, false)
                UserViewHolder(binding)
            }
            TYPE_LOADING -> {
                val binding = ItemLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(binding)
            }
            TYPE_ERROR -> {
                val binding = ItemErrorBinding.inflate(inflater, parent, false)
                ErrorViewHolder(binding, onRetryClicked)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> holder.bind((getItem(position) as UiItem.ProductItem).product)
            is UserViewHolder -> holder.bind((getItem(position) as UiItem.UserItem).user)
            is LoadingViewHolder -> holder.bind()
            is ErrorViewHolder -> holder.bind((getItem(position) as UiItem.ErrorItem).message)
        }
    }

    class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(product: ProductDto) {
            binding.tvTitle.text = product.title
            binding.tvDescription.text = product.description
            binding.tvPrice.text = "$${product.price}"
        }
    }

    class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDto) {
            binding.tvUserName.text = "${user.firstname} ${user.lastname}"
            binding.tvUserEmail.text = user.email
        }
    }

    class LoadingViewHolder(private val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            // Loading state is handled by the layout
        }
    }

    class ErrorViewHolder(private val binding: ItemErrorBinding,
        private val onRetryClicked: (() -> Unit)?) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: String) {
            binding.tvErrorMessage.text = message
            binding.btnRetry.setOnClickListener {
                onRetryClicked?.invoke()
                // Retry logic will be handled by the activity
            }
        }
    }

}
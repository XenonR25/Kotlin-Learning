package com.example.assignment1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.databinding.ItemImageViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.LifecycleCoroutineScope

class ImageAdapter(private val images: List<ContentProviderFragment.ImageModel>, private val scope: LifecycleCoroutineScope // Add this
) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {


    class ViewHolder(val binding: ItemImageViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("GalleryCheck","Image is executing")
        val item = images[position]
        val context = holder.binding.root.context

        // 1. Reset image so old images don't show while loading
        holder.binding.itemImage.setImageBitmap(null)

        // 2. Load the actual image data
        // Use the passed-in scope instead of searching for one
        scope.launch(Dispatchers.IO) {
            android.util.Log.d("AdapterLog", "Coroutine EXECUTING for: ${item.uri}")
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver.loadThumbnail(item.uri, Size(300, 300), null)
                } else {
                    context.contentResolver.openInputStream(item.uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }

                withContext(Dispatchers.Main) {
                    holder.binding.itemImage.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                android.util.Log.e("AdapterLog", "Error: ${e.message}")
            }
        }
    }

    override fun getItemCount(): Int = images.size
}





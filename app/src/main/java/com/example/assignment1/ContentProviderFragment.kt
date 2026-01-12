package com.example.assignment1

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContentProviderFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            setupRecyclerView()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        recyclerView = view.findViewById(R.id.galleryRecyclerView)

        // OPTIONAL: Initialize with an empty adapter to stop the "No adapter attached" warning
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        checkAndRequestPermission(permission)
    }

    private fun checkAndRequestPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            setupRecyclerView()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun setupRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val images = fetchGalleryImages(requireContext())

            withContext(Dispatchers.Main) {
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
                // Now we set the adapter with the real data
                recyclerView.adapter = ImageAdapter(images, viewLifecycleOwner.lifecycleScope)
            }
        }
    }

    data class ImageModel(val uri: Uri)

    private fun fetchGalleryImages(context: Context): List<ImageModel> {
        val imageList = mutableListOf<ImageModel>()
        val projection = arrayOf(MediaStore.Images.Media._ID)

        val queryUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            queryUri,
            projection, null, null, sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)

                // FIX: Use EXTERNAL_CONTENT_URI for individual item URIs
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imageList.add(ImageModel(contentUri))
            }
        }

        // DEBUG: Check your Logcat to see if this number is greater than 0
        android.util.Log.d("GalleryQuery", "Found ${imageList.size} images")

        return imageList
    }
}
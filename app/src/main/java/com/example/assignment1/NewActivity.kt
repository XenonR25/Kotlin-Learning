// NewActivity.kt
package com.example.assignment1

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.viewModel.ProductViewModel
import com.example.assignment1.viewModel.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewActivity : AppCompatActivity() {
    private val viewModel: ProductViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnLoadData: Button
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        btnLoadData = findViewById(R.id.btnLoadData)

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // Initial state
        updateUI(UiState.Success(emptyList()))
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(onRetryClicked = {
            viewModel.loadData()
        })
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        btnLoadData.setOnClickListener {
            viewModel.loadData()
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: UiState) {
        when (state) {
            is UiState.Loading -> {
                showLoadingState()
            }
            is UiState.Success -> {
                showSuccessState(state.items)
            }
            is UiState.Error -> {
                showErrorState(state.message)
            }
        }
    }

    private fun showLoadingState() {
        progressBar.visibility = View.VISIBLE
        btnLoadData.isEnabled = false
        btnLoadData.text = "Loading..."

        // Show loading item in recyclerview
        adapter.submitList(listOf(com.example.assignment1.DataClass.UiItem.LoadingItem))
    }

    private fun showSuccessState(items: List<com.example.assignment1.DataClass.UiItem>) {
        progressBar.visibility = View.GONE
        btnLoadData.isEnabled = true
        btnLoadData.text = "Refresh Data"

        adapter.submitList(items)
    }

    private fun showErrorState(message: String) {
        progressBar.visibility = View.GONE
        btnLoadData.isEnabled = true
        btnLoadData.text = "Retry"

        adapter.submitList(listOf(com.example.assignment1.DataClass.UiItem.ErrorItem(message)))
    }
}
package com.giahung.lab3homework.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giahung.lab3homework.data.Image
import com.giahung.lab3homework.data.PixabayApi
import com.giahung.lab3homework.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ImageViewModel : ViewModel() {
    private val apiKey = "49908855-5ec2fe26faa4731f807666e5f" // Replace with your Pixabay API key
    private val query = "nature"
    private val perPage = 20

    private val _images = MutableStateFlow<List<Image>>(emptyList())
    val images: StateFlow<List<Image>> = _images

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchImages(1)
    }

    fun fetchImages(page: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.searchImages(apiKey, query, page, perPage)
                _images.value = response.hits
                _currentPage.value = page
            } catch (e: Exception) {
                _images.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun nextPage() {
        fetchImages(_currentPage.value + 1)
    }

    fun previousPage() {
        if (_currentPage.value > 1) {
            fetchImages(_currentPage.value - 1)
        }
    }
}
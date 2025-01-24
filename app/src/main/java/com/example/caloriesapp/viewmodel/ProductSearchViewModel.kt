package com.example.caloriesapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caloriesapp.data.model.Product
import com.example.caloriesapp.ui.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductSearchViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> get() = _selectedProduct

    fun updateSearchQuery(query: String) {
        Log.d("ProductSearchViewModel", "Updating search query: $query")
        _searchQuery.value = query
        viewModelScope.launch {
            delay(300)
            searchProducts(query)
        }
    }

    private fun searchProducts(query: String) {
        viewModelScope.launch {
            val allProducts = loadProductsFromNetwork()
            val filteredProducts = allProducts
                .filter { product ->
                    product.name.contains(query, ignoreCase = true)
                }
                .sortedBy { product ->
                    when {
                        product.name.startsWith(query, ignoreCase = true) -> 0
                        else -> 1 
                    }
                }
            _products.value = filteredProducts
        }
    }

    fun selectProduct(product: Product) {
        _selectedProduct.value = product
    }

    private suspend fun loadProductsFromNetwork(): List<Product> {
        return try {
            RetrofitClient.instance.getProducts()
        } catch (e: Exception) {
            Log.e("ProductSearchViewModel", "Error loading products", e)
            emptyList()
        }
    }
}
package com.example.caloriesapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caloriesapp.data.model.Product
import com.example.caloriesapp.data.model.SavedProduct
import com.example.caloriesapp.data.repository.SavedProductRepository
import com.example.caloriesapp.ui.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Состояния экрана:
 * - Search: Экран поиска продуктов.
 * - ProductSelected: Экран с выбранным продуктом.
 */
sealed class ScreenState {
    object Search : ScreenState() // Состояние поиска
    data class ProductSelected(val product: Product) : ScreenState() // Состояние выбранного продукта
}

/**
 * ViewModel для экрана поиска продуктов.
 * Управляет состоянием экрана, поиском и выбором продуктов.
 */
class AppViewModel(application: Application) : AndroidViewModel(application)  {

    // Репозиторий для работы с базой данных
    private val repository = SavedProductRepository(application)

    // Состояния из ProductSearchViewModel
    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Search)
    val screenState: StateFlow<ScreenState> get() = _screenState

    private val _showSnackbar = MutableStateFlow(false)
    val showSnackbar: StateFlow<Boolean> get() = _showSnackbar

    private val _savedProduct = MutableStateFlow<SavedProduct?>(null)
    val savedProduct: StateFlow<SavedProduct?> get() = _savedProduct

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    private val _weightProduct = MutableStateFlow("")
    val weightProduct: StateFlow<String> get() = _weightProduct

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> get() = _selectedProduct

    // Состояния из SavedProductViewModel
    private val _allSavedProducts = MutableStateFlow<List<SavedProduct>>(emptyList())
    val allSavedProducts: StateFlow<List<SavedProduct>> get() = _allSavedProducts

    // Методы из ProductSearchViewModel
    fun updateWeightProduct(newWeight: String) {
        _weightProduct.value = newWeight
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            delay(30)
            searchProducts(query)
        }
    }

    fun clearFields() {
        _weightProduct.value = ""
        _selectedProduct.value = null
        _searchQuery.value = ""
    }

    fun selectProduct(product: Product) {
        _selectedProduct.value = product
        _searchQuery.value = product.name
        _products.value = emptyList()
    }

    fun saveProductData(weight: String, product: Product?) {
        viewModelScope.launch(Dispatchers.IO) { // Используем Dispatchers.IO для работы с базой данных
            try {
                if (product == null) {
                    Log.e("CombinedViewModel", "Product is null")
                    return@launch
                }
                Log.d("CombinedViewModel", "Saving data: weight=$weight, product=${product.name}")

                // Создаём объект SavedProduct
                val savedProduct = SavedProduct(
                    name = product.name,
                    bgu = product.bgu,
                    kcal = product.kcal,
                    weight = weight
                )

                // Сохраняем его в переменную
                _savedProduct.value = savedProduct

                // Вставляем продукт в базу данных
                repository.insert(savedProduct)
                loadAllSavedProducts()

                // Показываем Snackbar (возвращаемся на главный поток)
                withContext(Dispatchers.Main) {
                    _showSnackbar.value = true
                    Log.d("CombinedViewModel", "Snackbar state: ${_showSnackbar.value}")
                }

                // Очищаем поля
                clearFields()
            } catch (e: Exception) {
                Log.e("CombinedViewModel", "Error saving product data", e)
            }
        }
    }

    fun hideSnackbar() {
        _showSnackbar.value = false
    }

    // Методы из SavedProductViewModel
    fun loadAllSavedProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _allSavedProducts.value = repository.getAll()
        }
    }

    fun insert(savedProduct: SavedProduct) {
        viewModelScope.launch {
            repository.insert(savedProduct)
            loadAllSavedProducts()
        }
    }

    fun deleteById(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
            loadAllSavedProducts()
        }
    }

    private suspend fun searchProducts(query: String) {
        val allProducts = loadProductsFromNetwork()
        _products.value = allProducts
            .filter { it.name.contains(query, ignoreCase = true) }
            .sortedBy { if (it.name.startsWith(query, ignoreCase = true)) 0 else 1 }
    }

    private suspend fun loadProductsFromNetwork(): List<Product> {
        return try {
            RetrofitClient.instance.getProducts()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
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
class ProductSearchViewModel : ViewModel() {

    // Состояние экрана (поиск или выбранный продукт)
    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Search)
    val screenState: StateFlow<ScreenState> get() = _screenState

    // Список продуктов
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    // Поисковый запрос
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    // Выбранный продукт
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> get() = _selectedProduct

    /**
     * Возврат к экрану поиска.
     */
    fun returnToSearch() {
        _screenState.value = ScreenState.Search
    }

    /**
     * Обновление поискового запроса.
     *
     * @param query Новый поисковый запрос.
     */
    fun updateSearchQuery(query: String) {
        Log.d("ProductSearchViewModel", "Updating search query: $query")
        _searchQuery.value = query
        viewModelScope.launch {
            delay(300) // Задержка для дебаунса (чтобы не делать запросы на каждый символ)
            searchProducts(query) // Поиск продуктов после задержки
        }
    }

    /**
     * Поиск продуктов по запросу.
     *
     * @param query Поисковый запрос.
     */
    private fun searchProducts(query: String) {
        viewModelScope.launch {
            // Загрузка всех продуктов из сети
            val allProducts = loadProductsFromNetwork()
            // Фильтрация и сортировка продуктов
            val filteredProducts = allProducts
                .filter { product ->
                    product.name.contains(query, ignoreCase = true) // Фильтрация по названию
                }
                .sortedBy { product ->
                    when {
                        product.name.startsWith(query, ignoreCase = true) -> 0 // Продукты, начинающиеся с запроса, идут первыми
                        else -> 1 // Остальные продукты
                    }
                }
            _products.value = filteredProducts // Обновление списка продуктов
        }
    }

    /**
     * Выбор продукта.
     *
     * @param product Выбранный продукт.
     */
    fun selectProduct(product: Product) {
        Log.d("ProductSearchViewModel", "Product selected: ${product.name}")
        _screenState.value = ScreenState.ProductSelected(product) // Обновление состояния экрана
        Log.d("ProductSearchViewModel", "New screen state: ${_screenState.value}")
    }

    /**
     * Загрузка продуктов из сети.
     *
     * @return Список продуктов.
     */
    private suspend fun loadProductsFromNetwork(): List<Product> {
        return try {
            RetrofitClient.instance.getProducts() // Загрузка продуктов через Retrofit
        } catch (e: Exception) {
            Log.e("ProductSearchViewModel", "Error loading products", e) // Логирование ошибки
            emptyList() // Возвращаем пустой список в случае ошибки
        }
    }
}
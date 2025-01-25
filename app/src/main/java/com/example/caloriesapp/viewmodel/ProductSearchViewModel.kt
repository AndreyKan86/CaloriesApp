package com.example.caloriesapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caloriesapp.data.model.Product
import com.example.caloriesapp.data.model.SavedProduct
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

    // Состояние для отображения Snackbar
    private val _showSnackbar = MutableStateFlow(false)
    val showSnackbar: StateFlow<Boolean> get() = _showSnackbar

    // Переменная для хранения одного сохранённого продукта
    private val _savedProduct = MutableStateFlow<SavedProduct?>(null)
    val savedProduct: StateFlow<SavedProduct?> get() = _savedProduct

    // Список продуктов
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    //Вес продукта
    private val _weightProduct = MutableStateFlow("")
    val weightProduct: StateFlow<String> get() = _weightProduct

    // Поисковый запрос
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    // Выбранный продукт
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> get() = _selectedProduct

    //Обновляем вес
    fun updateWeightProduct(newWeight: String) {
        _weightProduct.value = newWeight
    }

    /**
     * Обновление поискового запроса.
     *
     * @param query Новый поисковый запрос.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            delay(30) // Задержка для дебаунса (чтобы не делать запросы на каждый символ)
            searchProducts(query) // Поиск продуктов после задержки
        }
    }

    // Очистка полей
    fun clearFields() {
        _weightProduct.value = ""
        _selectedProduct.value = null
        _searchQuery.value = ""
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
        _selectedProduct.value = product // Сохраняем выбранный продукт
        _searchQuery.value = product.name // Обновляем строку поиска
        _products.value = emptyList() // Очищаем список продуктов
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
            emptyList() // Возвращаем пустой список в случае ошибки
        }
    }


    fun saveProductData(weight: String, product: Product?) {
        viewModelScope.launch {
            try {
                if (product == null) {
                    Log.e("ProductSearchViewModel", "Product is null")
                    return@launch
                }
                Log.d("ProductSearchViewModel", "Saving data: weight=$weight, product=${product.name}")

                // Создаём объект SavedProduct
                val savedProduct = SavedProduct(product, weight)

                // Сохраняем его в переменную
                _savedProduct.value = savedProduct

                // Показываем Snackbar
                _showSnackbar.value = true

                // Очищаем поля
                clearFields()
            } catch (e: Exception) {
                Log.e("ProductSearchViewModel", "Error saving product data", e)
            }
        }
    }

    // Метод для скрытия Snackbar
    fun hideSnackbar() {
        _showSnackbar.value = false
    }

}


package com.example.caloriesapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caloriesapp.data.model.BguData
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


sealed class ScreenState {
    object Search : ScreenState() // Состояние поиска
    data class ProductSelected(val product: Product) : ScreenState() // Состояние выбранного продукта
}


class AppViewModel(application: Application) : AndroidViewModel(application)  {

    private val repository = SavedProductRepository(application)

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

    private val _allSavedProducts = MutableStateFlow<List<SavedProduct>>(emptyList())
    val allSavedProducts: StateFlow<List<SavedProduct>> get() = _allSavedProducts

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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (product == null) {
                    Log.e("CombinedViewModel", "Product is null")
                    return@launch
                }
                Log.d("CombinedViewModel", "Saving data: weight=$weight, product=${product.name}")

                val bguData = BGU(product.bgu)

                val kcalForSave = product.kcal.replace(",", ".").toDoubleOrNull()?:0.0
                val proteinForSave = bguData.protein.replace(",", ".").toDoubleOrNull()?:0.0
                val fatsForSave = bguData.fats.replace(",", ".").toDoubleOrNull()?:0.0
                val carbohydratesForSave = bguData.carbohydrates.replace(",", ".").toDoubleOrNull()?:0.0
                val weightForSave = weight.replace(",", ".").toDoubleOrNull()?:0.0

                val savedProduct = SavedProduct(
                    name = product.name,
                    kcal = (kcalForSave * weightForSave / 100).toString(),
                    protein = (proteinForSave * weightForSave / 100).toString(),
                    fats = (fatsForSave * weightForSave / 100).toString(),
                    carbohydrates = (carbohydratesForSave * weightForSave / 100).toString(),
                    weight = weightForSave.toString()
                )

                _savedProduct.value = savedProduct

                repository.insert(savedProduct)
                loadAllSavedProducts()

                withContext(Dispatchers.Main) {
                    _showSnackbar.value = true
                    Log.d("CombinedViewModel", "Snackbar state: ${_showSnackbar.value}")
                }

                clearFields()
            } catch (e: Exception) {
                Log.e("CombinedViewModel", "Error saving product data", e)
            }
        }
    }

    fun hideSnackbar() {
        _showSnackbar.value = false
    }

    fun loadAllSavedProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _allSavedProducts.value = repository.getAll()
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

    fun BGU(bgu: String): BguData{
        val parts = bgu.split(",")
        if (parts.size != 3) {
            val protein = ""
            val fats = ""
            val carbohydrates = ""
            return BguData(protein, fats, carbohydrates)
        }
        return try {
            val protein = parts[0].trim()
            val fats = parts[1].trim()
            val carbohydrates = parts[2].trim()
            BguData(protein, fats, carbohydrates)
        } catch (e: NumberFormatException) {
            val protein = ""
            val fats = ""
            val carbohydrates = ""
            BguData(protein, fats, carbohydrates)
        }
    }

    fun calculateTotals(products: List<SavedProduct>): Map<String, Double> {
        var totalKcal = 0.0
        var totalProtein = 0.0
        var totalFats = 0.0
        var totalCarbohydrates = 0.0
        var totalWeight = 0.0

        products.forEach { product ->
            totalKcal += product.kcal.toDoubleOrNull() ?: 0.0
            totalProtein += (product.protein).toDouble()
            totalFats += (product.fats).toDouble()
            totalCarbohydrates += (product.carbohydrates).toDouble()
            totalWeight += product.weight.toDoubleOrNull() ?: 0.0
        }

        return mapOf(
            "totalKcal" to totalKcal,
            "totalProtein" to totalProtein,
            "totalFats" to totalFats,
            "totalCarbohydrates" to totalCarbohydrates,
            "totalWeight" to totalWeight
        )
    }

    fun deleteProduct(product: SavedProduct) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(product.id)
            loadAllSavedProducts()
        }
    }
}
package com.example.caloriesapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caloriesapp.data.model.SavedProduct
import com.example.caloriesapp.data.repository.SavedProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SavedProductViewModel(application: Application) : AndroidViewModel(application) {

    // Репозиторий для работы с базой данных
    private val repository = SavedProductRepository(application)

    // StateFlow для наблюдения за списокм продуктов
    private val _allProducts = MutableStateFlow<List<SavedProduct>>(emptyList())
    val allProducts: StateFlow<List<SavedProduct>> get() = _allProducts

    fun loadAllProducts() {
        viewModelScope.launch(Dispatchers.IO) { // Используем Dispatchers.IO для работы с базой данных
            _allProducts.value = repository.getAll()
        }
    }

    // Добавить новый продукт
    fun insert(savedProduct: SavedProduct) {
        viewModelScope.launch {
            repository.insert(savedProduct)
            loadAllProducts() // Обновляем список после добавления
        }
    }

    // Удалить продукт по ID
    fun deleteById(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
            loadAllProducts() // Обновляем список после удаления
        }
    }
}
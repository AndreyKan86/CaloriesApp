package com.example.caloriesapp.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caloriesapp.ui.viewmodel.SavedProductViewModel

@Composable
fun ProductListScreen(viewModel: SavedProductViewModel = viewModel()) {
    // Получаем данные из ViewModel
    val products by viewModel.allProducts.collectAsState()

    // Загружаем данные при первом запуске
    LaunchedEffect(Unit) {
        viewModel.loadAllProducts()
    }

    // Отображаем список продуктов
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(products) { product ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Название: ${product.name}")
                Text(text = "Калории: ${product.kcal.toDouble()*product.weight.toDouble()}")
                Text(text = "Вес: ${product.weight}")
            }
        }
    }
}
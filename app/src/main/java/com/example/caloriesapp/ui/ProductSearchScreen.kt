package com.example.caloriesapp.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.example.caloriesapp.data.model.Product
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caloriesapp.viewmodel.ProductSearchViewModel
import com.example.caloriesapp.viewmodel.ScreenState

/**
 * Главный экран поиска продуктов.
 * Отображает либо экран поиска, либо экран выбранного продукта в зависимости от состояния.
 *
 * @param viewModel ViewModel, которая управляет состоянием экрана.
 */
@Composable
fun ProductSearchScreen(viewModel: ProductSearchViewModel) {


    // Получаем текущее состояние экрана из ViewModel
    val screenState by viewModel.screenState.collectAsState()
    // Основной контейнер для отображения содержимого экрана
    Column {
        // В зависимости от состояния экрана отображаем либо экран поиска, либо экран выбранного продукта
        when (val state = screenState) {
            is ScreenState.Search -> {
                SearchScreen(viewModel) // Экран поиска продуктов
            }

            is ScreenState.ProductSelected -> {
                viewModel.returnToSearch() // Функция для возврата к поиску
            }
        }
    }
}

/**
 * Элемент списка продуктов.
 * Отображает карточку с названием продукта и обрабатывает нажатие на неё.
 *
 * @param product Продукт, который нужно отобразить.
 * @param onClick Функция, которая вызывается при нажатии на продукт.
 */
@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    // Карточка продукта
    Card(
        modifier = Modifier
            .fillMaxWidth() // Занимает всю доступную ширину
            .clickable {
                Log.d("ProductItem", "Product clicked: ${product.name}") // Логируем нажатие
                onClick() // Вызываем переданную функцию onClick
            }
            .padding(8.dp) // Отступы вокруг карточки
    ) {
        // Текст с названием продукта
        Text(
            text = "${product.name}",
            modifier = Modifier.padding(16.dp) // Отступы внутри карточки
        )
    }
}


@Composable
fun SearchBar(viewModel: ProductSearchViewModel) {
    // Подписываемся на searchQuery из ViewModel
    val searchQuery by viewModel.searchQuery.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchQuery, // Используем значение из StateFlow
            onValueChange = { newQuery ->
                viewModel.updateSearchQuery(newQuery) // Обновляем значение в ViewModel
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            label = { Text("Введите название продукта") }
        )
        Button(
            onClick = { /* Обработка нажатия */ }
        ) {
            Text("Поиск")
        }
    }
}

/**
 * Экран поиска продуктов.
 * Содержит поле для ввода поискового запроса и список продуктов.
 *
 * @param viewModel ViewModel, которая управляет состоянием экрана.
 */
@Composable
fun SearchScreen(viewModel: ProductSearchViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val products by viewModel.products.collectAsState()

    Column(modifier = Modifier.padding(16.dp))
    {
        Card() {
            TextField(
                value = searchQuery,
                onValueChange = { newQuery ->
                    viewModel.updateSearchQuery(newQuery)
                },
                label = { Text("Введите название продукта") },
                modifier = Modifier.fillMaxWidth(2 / 3f)
            )
        }
            // Отображаем список продуктов только если он не пустой
        if (products.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxWidth(2 / 3f)) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onClick = { viewModel.selectProduct(product) }
                    )
                }
            }
        }
    }
}
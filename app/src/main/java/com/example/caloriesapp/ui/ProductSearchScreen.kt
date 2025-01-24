package com.example.caloriesapp.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import com.example.caloriesapp.data.model.Product
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

    // Логируем текущее состояние экрана для отладки
    Log.d("ProductSearchScreen", "Current screen state: $screenState")

    // Основной контейнер для отображения содержимого экрана
    Column {
        // Временный текст для отладки, отображающий текущее состояние экрана
        Text(text = "Debug: $screenState")

        // В зависимости от состояния экрана отображаем либо экран поиска, либо экран выбранного продукта
        when (val state = screenState) {
            is ScreenState.Search -> {
                SearchScreen(viewModel) // Экран поиска продуктов
            }
            is ScreenState.ProductSelected -> {
                SelectedProductScreen(
                    product = state.product, // Выбранный продукт
                    onBack = { viewModel.returnToSearch() } // Функция для возврата к поиску
                )
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

/**
 * Экран поиска продуктов.
 * Содержит поле для ввода поискового запроса и список продуктов.
 *
 * @param viewModel ViewModel, которая управляет состоянием экрана.
 */
@Composable
fun SearchScreen(viewModel: ProductSearchViewModel) {
    // Получаем текущий поисковый запрос из ViewModel
    val searchQuery by viewModel.searchQuery.collectAsState()
    // Получаем список продуктов из ViewModel
    val products by viewModel.products.collectAsState()

    // Основной контейнер для отображения содержимого экрана
    Column(modifier = Modifier.padding(16.dp)) {
        // Поле для ввода поискового запроса
        TextField(
            value = searchQuery, // Текущий текст в поле ввода
            onValueChange = { newQuery ->
                viewModel.updateSearchQuery(newQuery) // Обновляем поисковый запрос в ViewModel
            },
            label = { Text("Введите название продукта") }, // Подсказка в поле ввода
            modifier = Modifier.fillMaxWidth() // Занимает всю доступную ширину
        )

        // Список продуктов
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            // Отображаем каждый продукт в списке
            items(products) { product ->
                ProductItem(
                    product = product, // Продукт для отображения
                    onClick = { viewModel.selectProduct(product) } // Обработка нажатия на продукт
                )
            }
        }
    }
}

/**
 * Экран выбранного продукта.
 * Отображает информацию о выбранном продукте и кнопку для возврата к поиску.
 *
 * @param product Выбранный продукт.
 * @param onBack Функция, которая вызывается при нажатии на кнопку "Назад к поиску".
 */
@Composable
fun SelectedProductScreen(product: Product, onBack: () -> Unit) {
    // Основной контейнер для отображения содержимого экрана
    Column(
        modifier = Modifier
            .fillMaxSize() // Занимает весь доступный экран
            .padding(16.dp), // Отступы вокруг контейнера
        horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру по горизонтали
        verticalArrangement = Arrangement.Center // Выравнивание по центру по вертикали
    ) {
        // Название выбранного продукта
        Text(
            text = "Выбранный продукт: ${product.name}",
            style = MaterialTheme.typography.headlineMedium, // Стиль текста
            modifier = Modifier.padding(8.dp) // Отступы вокруг текста
        )
        // Калорийность продукта
        Text(
            text = "Калорийность: ${product.kcal} ккал",
            style = MaterialTheme.typography.bodyLarge, // Стиль текста
            modifier = Modifier.padding(8.dp) // Отступы вокруг текста
        )
        // Кнопка для возврата к поиску
        Button(
            onClick = onBack, // Обработка нажатия на кнопку
            modifier = Modifier.padding(16.dp) // Отступы вокруг кнопки
        ) {
            Text("Назад к поиску") // Текст на кнопке
        }
    }
}
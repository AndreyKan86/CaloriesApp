package com.example.caloriesapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.example.caloriesapp.data.model.Product
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caloriesapp.viewmodel.ProductSearchViewModel
import kotlinx.coroutines.delay

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
    // Состояние для отображения Snackbar
    val showSnackbar by viewModel.showSnackbar.collectAsState()
    // Сохранённый продукт
    val savedProduct by viewModel.savedProduct.collectAsState()
    // Контроллер для управления экранной клавиатурой
    val keyboardController = LocalSoftwareKeyboardController.current
    // Менеджер фокуса
    val focusManager = LocalFocusManager.current
    // FocusRequester для поля ввода веса
    val focusRequester = remember { FocusRequester() }

    // Автоматическое скрытие Snackbar через 4 секунды
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            delay(4000) // 4 секунды
            viewModel.hideSnackbar()
            keyboardController?.hide() // Скрываем клавиатуру
            focusManager.clearFocus() // Убираем фокус с текстового поля
        }
    }

    // Основной контейнер для отображения содержимого экрана
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Верхняя часть: поиск, поле для веса и кнопка
        SearchBar(viewModel, focusManager, focusRequester)

        // Нижняя часть: выпадающий список (если есть продукты)
        val products by viewModel.products.collectAsState()
        if (products.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f) // Занимает оставшееся пространство
            ) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onClick = {
                            viewModel.selectProduct(product)
                            focusRequester.requestFocus() // Перемещаем фокус в поле ввода веса
                        }
                    )
                }
            }
        }
    }

    // Отображение Snackbar
    if (showSnackbar) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter // Размещаем Snackbar внизу экрана
        ) {
            Snackbar(
                action = {
                    Button(
                        onClick = {
                            viewModel.hideSnackbar() // Скрываем Snackbar
                            keyboardController?.hide() // Скрываем клавиатуру
                            focusManager.clearFocus() // Убираем фокус с текстового поля
                        }
                    ) {
                        Text("OK")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                // Отображаем информацию о сохранённом продукте
                savedProduct?.let { product ->
                    Text("Сохранён продукт: ${product.product.name}, вес: ${product.weight}")
                }
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

// Строка с полем поиска, кнопкой добавления и весом
@Composable
fun SearchBar(viewModel: ProductSearchViewModel, focusManager: FocusManager, focusRequester: FocusRequester) {

    val searchQuery by viewModel.searchQuery.collectAsState()
    val weightProduct by viewModel.weightProduct.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()

    Column {
        // Поиск и поле для веса
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically, // Выравниваем элементы по вертикали
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            searchBox(viewModel, focusManager)
            weightProduct(viewModel, focusManager, focusRequester)
            addButton(viewModel, focusManager)
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
fun searchBox(viewModel: ProductSearchViewModel, focusManager: FocusManager) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val products by viewModel.products.collectAsState()

    Column(modifier = Modifier.padding(5.dp)) {
        Card {
            TextField(
                value = searchQuery,
                onValueChange = { newQuery ->
                    viewModel.updateSearchQuery(newQuery)
                },
                label = { Text("Введите название продукта") },
                modifier = Modifier.fillMaxWidth(0.65f)
            )
        }
    }
}

// Окно для ввода веса продуктов
@Composable
fun weightProduct(viewModel: ProductSearchViewModel, focusManager: FocusManager, focusRequester: FocusRequester) {
    val weightProduct by viewModel.weightProduct.collectAsState()
    Card {
        TextField(
            value = weightProduct,
            onValueChange = { viewModel.updateWeightProduct(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .focusRequester(focusRequester), // Привязываем FocusRequester
            singleLine = true
        )
    }
}

@Composable
fun addButton(viewModel: ProductSearchViewModel, focusManager: FocusManager) {
    val weightProduct by viewModel.weightProduct.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd // Выравниваем кнопку в верхнем правом углу
    ) {
        Button(
            onClick = {
                viewModel.saveProductData(weightProduct, selectedProduct)
                viewModel.clearFields()
                focusManager.clearFocus() // Убираем фокус с текстового поля
            },
            modifier = Modifier
                .padding(top = 4.dp)
                .size(56.dp)
                .clip(CircleShape)
        ) {
            Text(
                text = "+",
                color = Color.White,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
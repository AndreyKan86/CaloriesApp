package com.example.caloriesapp.ui.composable

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.example.caloriesapp.data.model.Product
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.caloriesapp.data.model.SavedProduct
import com.example.caloriesapp.viewmodel.ProductSearchViewModel
import kotlinx.coroutines.delay

//Функция вызова экрана поиска
@Composable
fun ProductSearchScreen(viewModel: ProductSearchViewModel) {
    val showSnackbar by viewModel.showSnackbar.collectAsState()
    val savedProduct by viewModel.savedProduct.collectAsState()
    val products by viewModel.products.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            delay(2000)
            viewModel.hideSnackbar()
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(viewModel, focusManager, focusRequester)
        ProductList(
            products = products,
            onProductClick = { product ->
                viewModel.selectProduct(product)
                focusRequester.requestFocus()
            }
        )
    }

    ProductSnackbar(
        showSnackbar = showSnackbar,
        savedProduct = savedProduct,
        onDismiss = {
            viewModel.hideSnackbar()
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    )
}

//Функция всплывающего окна
@Composable
private fun ProductSnackbar(
    showSnackbar: Boolean,
    savedProduct: SavedProduct?,
    onDismiss: () -> Unit
) {
    if (showSnackbar) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                action = {
                    Button(onClick = onDismiss) {
                        Text("OK")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                savedProduct?.let { product ->
                    Text("Сохранён продукт: ${product.name}, вес: ${product.weight}")
                }
            }
        }
    }
}

//Функция выпадающего списка продуктов
@Composable
private fun ProductList(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    if (products.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .zIndex(1f) // Поднимаем список выше
                    .alpha(0.7f) // Полупрозрачность
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // Ограничиваем высоту
            ) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onClick = { onProductClick(product) }
                    )
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
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(8.dp)
    ) {
        Text(
            text = "${product.name}",
            modifier = Modifier.padding(16.dp)
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            searchBox(viewModel, focusManager)
            weightProduct(viewModel, focusManager, focusRequester)
            addButton(viewModel, focusManager)
        }
    }
}

//Окно поиска продуктов
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

//Кнопка добавления продуктов
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


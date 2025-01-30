package com.example.caloriesapp.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import com.example.caloriesapp.data.model.Product
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.caloriesapp.data.model.SavedProduct
import com.example.caloriesapp.viewmodel.AppViewModel
import kotlinx.coroutines.delay

//Функция вызова экрана поиска
@Composable
fun ProductSearchScreen(viewModel: AppViewModel) {
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
fun ProductList(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    if (products.isNotEmpty()) {
        Box() {
            LazyColumn(
                modifier = Modifier
                    .zIndex(1f)
                    .alpha(0.9f)
                    .fillMaxWidth()
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

//Элемент списка продуктов
@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
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
            modifier = Modifier.padding(16.dp),
            color = Color.Red,
            fontFamily = FontFamily.Serif
        )
    }
}

// Строка с полем поиска, кнопкой добавления и весом
@Composable
fun SearchBar(viewModel: AppViewModel, focusManager: FocusManager, focusRequester: FocusRequester) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            val (box1, box2, box3) = createRefs()
            Box(
                modifier = Modifier.constrainAs(box1){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(box2.start)
                    width = Dimension.fillToConstraints
                }
            )
            {
                searchBox(viewModel)
            }
            Box(
                modifier = Modifier.constrainAs(box2){
                    top.linkTo(parent.top)
                    start.linkTo(box1.end, margin = 16.dp)
                    end.linkTo(box3.start)
                    width = Dimension.value(80.dp)
                }
            )
            {
                weightProduct(viewModel, focusRequester)
            }
            Box(
                modifier = Modifier.constrainAs(box3){
                    top.linkTo(parent.top)
                    start.linkTo(box2.end, margin = 16.dp)
                    end.linkTo(parent.end)
                    width = Dimension.value(56.dp)
                }
            )
            {
                addButton(viewModel, focusManager)
            }
        }
}

//Окно поиска продуктов
@Composable
fun searchBox(viewModel: AppViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
        Card() {
            TextField(
                value = searchQuery,
                onValueChange = { newQuery ->
                    viewModel.updateSearchQuery(newQuery)
                },
                label = { Text("Введите название продукта") },
                modifier = Modifier.fillMaxWidth()
            )
        }
}

// Окно для ввода веса продуктов
@Composable
fun weightProduct(viewModel: AppViewModel, focusRequester: FocusRequester) {
    val weightProduct by viewModel.weightProduct.collectAsState()
    Card {
        TextField(
            value = weightProduct,
            onValueChange = { viewModel.updateWeightProduct(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true
        )
    }
}

//Кнопка добавления продуктов
@Composable
fun addButton(viewModel: AppViewModel, focusManager: FocusManager) {
    val weightProduct by viewModel.weightProduct.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()


        Button(
            onClick = {
                viewModel.saveProductData(weightProduct, selectedProduct)
                viewModel.clearFields()
                focusManager.clearFocus()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Text(
                text = "+",
                color = Color.White,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
        }
}


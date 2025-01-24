package com.example.caloriesapp.ui

import androidx.compose.foundation.clickable
import com.example.caloriesapp.data.model.Product
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caloriesapp.viewmodel.ProductSearchViewModel

@Composable
fun ProductSearchScreen(
    viewModel: ProductSearchViewModel = viewModel(),
    onProductSelected: (Product) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val products by viewModel.products.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Введите название продукта") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn {
            items(products) { product ->
                Text(
                    text = "${product.name}",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onProductSelected(product) }
                )
            }
        }
        selectedProduct?.let { product ->
            Text(
                text = "Выбран продукт: ${product.name}",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(
            text = "${product.name}",
            modifier = Modifier.padding(16.dp)
        )
    }
}
package com.example.caloriesapp.ui.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caloriesapp.viewmodel.AppViewModel

@Composable
fun SavedProductsScreen(viewModel: AppViewModel = viewModel()) {
    val allSavedProducts by viewModel.allSavedProducts.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.loadAllSavedProducts()
    }
    LazyColumn {
        items(allSavedProducts) { product ->
            Text(text = product.name)
        }
    }
}



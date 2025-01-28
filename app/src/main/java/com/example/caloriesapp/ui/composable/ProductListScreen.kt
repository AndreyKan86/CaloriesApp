package com.example.caloriesapp.ui.composable

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
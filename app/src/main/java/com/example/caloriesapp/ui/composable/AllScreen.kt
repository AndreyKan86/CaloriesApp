package com.example.caloriesapp.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caloriesapp.viewmodel.AppViewModel


@Composable
fun SplitScreen() {
    val firstViewModel: AppViewModel = viewModel()
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Green)
    ) {
        Box(modifier = Modifier.fillMaxSize()
            .background(Color.White),
            contentAlignment = Alignment.Center

        )
        {
            SavedProductsScreen(viewModel = firstViewModel)
        }

        Box(
        )
        {
            ProductSearchScreen(viewModel = firstViewModel)
        }
    }
}


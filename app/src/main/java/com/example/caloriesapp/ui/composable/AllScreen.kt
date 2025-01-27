package com.example.caloriesapp.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caloriesapp.ui.viewmodel.SavedProductViewModel
import com.example.caloriesapp.viewmodel.ProductSearchViewModel


//Функция для отображения двух экранов
@Composable
fun SplitScreen() {
    val firstViewModel: ProductSearchViewModel = viewModel()
    val secondViewModel: SavedProductViewModel = viewModel()

    Column(modifier = Modifier.fillMaxSize()
        /* .height(IntrinsicSize.Min)*/){
        // Верхняя половина экрана
        Box(modifier = Modifier
            .weight(1f)
            //.height(IntrinsicSize.Min) для минимизации экрана


        ) {
            ProductSearchScreen(viewModel = firstViewModel)
        }

        HorizontalDivider(color = Color.Black, thickness = 2.dp)

        Box(modifier = Modifier.weight(3f)) {
            ProductListScreen(viewModel = secondViewModel)
        }
    }
}


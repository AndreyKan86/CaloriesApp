package com.example.caloriesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caloriesapp.ui.composable.ProductSearchScreen
import com.example.caloriesapp.viewmodel.ProductSearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                // Создаем ViewModel
                val viewModel: ProductSearchViewModel = viewModel()
                // Вызов экрана поиска продуктов
                ProductSearchScreen(viewModel = viewModel)
            }
        }
    }
}
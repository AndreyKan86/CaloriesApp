package com.example.caloriesapp.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caloriesapp.data.model.BguData
import com.example.caloriesapp.data.model.SavedProduct
import com.example.caloriesapp.viewmodel.AppViewModel


@Composable
fun SavedProductsScreen(viewModel: AppViewModel = viewModel()) {

    val tableData = remember { mutableStateListOf<List<String>>() }
    tableData.clear()

    val allSavedProducts by viewModel.allSavedProducts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllSavedProducts()
    }

    allSavedProducts.forEach { product ->
        ProductItem(product = product, viewModel = viewModel, tableData = tableData)
    }

    Column {
        SixColumnTable(data = tableData)
    }
}

@Composable
fun ProductItem(product: SavedProduct, viewModel: AppViewModel, tableData: MutableList<List<String>>) {
    val bguData = viewModel.BGU(product.bgu)
    val protein = viewModel.convertBGU(bguData?.protein) * viewModel.convertBGU(product.weight) / 100.0
    val fats = viewModel.convertBGU(bguData?.fats) * viewModel.convertBGU(product.weight) / 100.0
    val carbohydrates = viewModel.convertBGU(bguData?.carbohydrates) * viewModel.convertBGU(product.weight) / 100.0
    tableData.add(
        listOf(
            product.name,
            product.kcal,
            String.format("%.3f",protein),
            String.format("%.3f",fats),
            String.format("%.3f", carbohydrates),
            product.weight
        )
    )
}

@Composable
fun SixColumnTable(data: List<List<String>>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(2.dp)
    ) {
        // Заголовок таблицы
        item { TableCell(text = "Name", isHeader = true) }
        item { TableCell(text = "kCal", isHeader = true) }
        item { TableCell(text = "Protein", isHeader = true) }
        item { TableCell(text = "Fats", isHeader = true) }
        item { TableCell(text = "Carbohydrates", isHeader = true) }
        item { TableCell(text = "Weight", isHeader = true) }

        // Данные таблицы
        items(data.size * 6) { index ->
            val rowIndex = index / 6
            val columnIndex = index % 6
            if (rowIndex < data.size && columnIndex < data[rowIndex].size) {
                TableCell(text = data[rowIndex][columnIndex])
            }
        }
    }
}


@Composable
fun TableCell(text: String, isHeader: Boolean = false) {
    Box(
        modifier = Modifier
            .background(if (isHeader) Color.Gray else Color.LightGray)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


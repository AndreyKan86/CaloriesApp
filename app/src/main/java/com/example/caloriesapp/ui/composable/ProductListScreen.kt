package com.example.caloriesapp.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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

    val totals = viewModel.calculateTotals(allSavedProducts)

    LaunchedEffect(Unit) {
        viewModel.loadAllSavedProducts()
    }

    allSavedProducts.forEach { product ->
        ProductItem(product = product, viewModel = viewModel, tableData = tableData)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SixColumnTableRow(
            isHeader = true,
            data = listOf("Name", "kCal", "Protein", "Fats", "Carbohydrates", "Weight"),
            weights = listOf(2f, 1f, 1f, 1f, 1f, 1f),
            headerBackgroundColor = Color.DarkGray, // Изменяем цвет фона заголовка
            headerTextColor = Color.White // Изменяем цвет текста заголовка
        )

        // Прокручиваемая область с данными
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(tableData.size) { index ->
                SixColumnTableRow(
                    data = tableData[index],
                    weights = listOf(2f, 1f, 1f, 1f, 1f, 1f),
                    rowBackgroundColor = Color.LightGray
                )
            }
        }

        // Итоговая строка (фиксированная строка)
        SixColumnTableRow(
            data = listOf(
                "Итого",
                String.format("%.2f", totals["totalKcal"]),
                String.format("%.2f", totals["totalProtein"]),
                String.format("%.2f", totals["totalFats"]),
                String.format("%.2f", totals["totalCarbohydrates"]),
                String.format("%.2f", totals["totalWeight"])
            ),
            weights = listOf(2f, 1f, 1f, 1f, 1f, 1f),
            rowBackgroundColor = Color.Red
        )
    }
}

@Composable
fun ProductItem(product: SavedProduct, viewModel: AppViewModel, tableData: MutableList<List<String>>) {

    tableData.add(
        listOf(
            product.name,
            product.kcal,
            String.format("%.2f",(product.protein).toDouble()),
            String.format("%.2f",(product.fats).toDouble()),
            String.format("%.2f", (product.carbohydrates.toDouble())),
            product.weight
        )
    )
}

@Composable
fun SixColumnTableRow(
    data: List<String>,
    isHeader: Boolean = false,
    weights: List<Float> = listOf(1f, 1f, 1f, 1f, 1f, 1f),
    headerBackgroundColor: Color = Color.Gray,
    headerTextColor: Color = Color.Black,
    headerFontWeight: FontWeight = FontWeight.Bold,
    rowBackgroundColor: Color = Color.LightGray,
    rowTextColor: Color = Color.Black,
    rowFontWeight: FontWeight = FontWeight.Normal
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        data.forEachIndexed { index, cellData ->
            TableCell(
                text = cellData,
                isHeader = isHeader,
                modifier = Modifier.weight(weights[index]),
                backgroundColor = if (isHeader) headerBackgroundColor else rowBackgroundColor,
                textColor = if (isHeader) headerTextColor else rowTextColor,
                fontWeight = if (isHeader) headerFontWeight else rowFontWeight
            )
        }
    }
}

@Composable
fun TableCell(
    text: String,
    isHeader: Boolean = false,
    modifier: Modifier = Modifier,
    backgroundColor: Color = if (isHeader) Color.Gray else Color.LightGray,
    textColor: Color = Color.Black,
    fontWeight: FontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .padding(8.dp)
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = textColor,
            fontWeight = fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Visible // Изменено

        )
    }
}
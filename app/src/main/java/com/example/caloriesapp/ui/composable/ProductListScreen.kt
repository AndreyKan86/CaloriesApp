package com.example.caloriesapp.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.onLongClick
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

    val allSavedProducts by viewModel.allSavedProducts.collectAsState()
    val totals = viewModel.calculateTotals(allSavedProducts)

    LaunchedEffect(Unit) {
        viewModel.loadAllSavedProducts()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SixColumnTableRow(
            isHeader = true,
            data = listOf("Name", "kCal", "Protein", "Fats", "Carbohydrates", "Weight"),
            weights = listOf(2f, 1f, 1f, 1f, 1f, 1f),
            headerBackgroundColor = Color.DarkGray,
            headerTextColor = Color.White
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(allSavedProducts, key = { it.id }) { product ->
                Column {
                    ProductItemRow(product = product, viewModel = viewModel)
                    HorizontalDivider(color = Color.Gray, thickness = 1.dp)
                }
            }
        }

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
            rowBackgroundColor = Color.DarkGray,
            rowTextColor = Color.White
        )
    }
}

@Composable
fun ProductItemRow(product: SavedProduct, viewModel: AppViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Удаление продукта") },
            text = { Text("Вы уверены, что хотите удалить ${product.name}?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteProduct(product)
                    showDialog = false
                }) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    SixColumnTableRow(
        data = listOf(
            product.name,
            String.format("%.2f", product.kcal.toDouble()),
            String.format("%.2f", product.protein.toDouble()),
            String.format("%.2f", product.fats.toDouble()),
            String.format("%.2f", product.carbohydrates.toDouble()),
            String.format("%.2f", product.weight.toDouble())
        ),
        weights = listOf(2f, 1f, 1f, 1f, 1f, 1f),
        rowBackgroundColor = Color.LightGray,
        onLongClick = { showDialog = true }
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
    rowFontWeight: FontWeight = FontWeight.Normal,
    onLongClick: () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        data.forEachIndexed { index, cellData ->
            TableCell(
                text = cellData,
                isHeader = isHeader,
                modifier = Modifier
                    .weight(weights[index])
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLongClick() },
                        )
                    },
                backgroundColor = if (isHeader) headerBackgroundColor else rowBackgroundColor,
                textColor = if (isHeader) headerTextColor else rowTextColor,
                fontWeight = if (isHeader) headerFontWeight else rowFontWeight,
                showDivider = index < data.size - 1
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
    fontWeight: FontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
    showDivider: Boolean = false
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .padding(8.dp)
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                color = textColor,
                fontWeight = fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier.weight(1f)
            )
            if (showDivider) {
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 4.dp)
                        .width(1.dp),
                    color = Color.Gray
                )
            }
        }
    }
}
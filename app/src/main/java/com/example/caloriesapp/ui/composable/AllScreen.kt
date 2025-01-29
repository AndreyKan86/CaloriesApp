package com.example.caloriesapp.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caloriesapp.viewmodel.AppViewModel


@Composable
fun SplitScreen() {
    val firstViewModel: AppViewModel = viewModel()
    ConstraintLayout( modifier = Modifier.background(Color.Green)
        .fillMaxWidth()
    ) {

        val (box1, box2) = createRefs()

        var box1Height by remember { mutableStateOf(0) }
        val density = LocalDensity.current

        Box( modifier = Modifier
            .constrainAs(box1) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end)
            }
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                box1Height = with(density) { coordinates.size.height }
            }
            .zIndex(1f)
        )
        {
            ProductSearchScreen(viewModel = firstViewModel)
            Text(text = "box1 height: $box1Height")
        }

        Box(modifier = Modifier
            .width(400.dp)
            .height(600.dp)
            .background(Color.Blue)
            .constrainAs(box2) {
                top.linkTo(parent.top, margin = 100.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .zIndex(0f),
            contentAlignment = Alignment.TopCenter
        )
        {
            SavedProductsScreen(viewModel = firstViewModel)
        }


    }
}






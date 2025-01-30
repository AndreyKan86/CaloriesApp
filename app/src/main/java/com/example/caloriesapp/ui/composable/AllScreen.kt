package com.example.caloriesapp.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caloriesapp.viewmodel.AppViewModel


@Composable
fun SplitScreen() {
    val firstViewModel: AppViewModel = viewModel()
    ConstraintLayout( modifier = Modifier
        .fillMaxSize()
    ) {
        val (box1, box2) = createRefs()
        Box( modifier = Modifier
            .constrainAs(box1) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .fillMaxWidth()
            .zIndex(1f)
        )
        {
            ProductSearchScreen(viewModel = firstViewModel)
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp)
            .padding(horizontal = 8.dp)
            .padding(bottom = 20.dp)
            .background(Color.LightGray)
            .constrainAs(box2) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
                height = androidx.constraintlayout.compose.Dimension.wrapContent
            }
            .zIndex(0f),
            contentAlignment = Alignment.TopCenter
        )
        {
            SavedProductsScreen(viewModel = firstViewModel)
        }


    }
}



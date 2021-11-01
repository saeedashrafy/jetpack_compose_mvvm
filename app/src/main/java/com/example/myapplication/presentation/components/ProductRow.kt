package com.example.myapplication.presentation.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import com.example.myapplication.R
import com.example.myapplication.domain.entity.Product
import com.example.myapplication.presentation.theme.LightGray
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState


@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ProductRow(
    navController: NavHostController,
    item: Product,
    modifier: Modifier = Modifier,
) {
    val imageSize = 72.dp
    val padding = 8.dp
    val itemHeight = imageSize + padding * 2


    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            shape = RoundedCornerShape(6.dp),
            modifier = modifier
                .requiredHeight(itemHeight)
                .padding(top = 8.dp)
                .fillMaxWidth(),
            elevation = 0.5.dp,
           onClick =  { navController.navigate("product_details/${item.id}") }
        ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.padding(padding),
            verticalAlignment = Alignment.CenterVertically
        ) {


            val painter = rememberCoilPainter(
                request = item.image,
                requestBuilder = { transformations(CircleCropTransformation()) },
                previewPlaceholder = R.drawable.ic_launcher_foreground,
            )

            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(LightGray)
                    .padding(10.dp)
                    .width(50.dp)
                    .height(50.dp),
                painter = painter,

                contentDescription = null
            )


            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
                modifier = modifier
                    .padding(start = 15.dp)
                    .fillMaxHeight()
            ) {
                Text(
                    item.nameFa,
                    style = MaterialTheme.typography.h3,
                    maxLines = 1,
                    textAlign = TextAlign.Right,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "خرید:",
                        maxLines = 1,

                        )
                    Text(
                        text = item.buyPrice + " تومان",
                        style = MaterialTheme.typography.h3,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

            }

        }
    }

    }

}




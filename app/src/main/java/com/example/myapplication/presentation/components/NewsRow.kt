package com.example.myapplication.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import coil.transform.CircleCropTransformation
import com.example.myapplication.domain.entity.News
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState

@Composable
fun NewsRow(item: News, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val imageHeight = 150.dp
    val padding = 8.dp
    val itemHeight = imageHeight + padding * 4


    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .padding(all = padding)
                .clickable(onClick = onClick)
        ) {

            item.image?.let {
                val painter = rememberCoilPainter(
                    request = "https://www.your_base_url.com$it",
                )
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .clip(RoundedCornerShape(10.dp)),
                    painter = painter,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = "null"
                )
            }


            Text(
                item.title,
                style = MaterialTheme.typography.h3,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(6.dp)
                    .fillMaxWidth()
            )
            item.subtractDate?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.subtitle1,
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .padding(6.dp)


                )
            }
            Divider(modifier = Modifier.fillMaxWidth())

        }

    }
}




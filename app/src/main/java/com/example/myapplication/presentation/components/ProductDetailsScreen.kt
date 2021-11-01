package com.example.myapplication.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.South
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.example.myapplication.R
import com.example.myapplication.domain.entity.Field
import com.example.myapplication.presentation.theme.DarkBlue
import com.example.myapplication.presentation.theme.DarkGray
import com.example.myapplication.presentation.theme.Gray
import com.example.myapplication.presentation.theme.LightGray
import com.example.myapplication.presentation.viewmodel.DetailsViewIntent
import com.example.myapplication.presentation.viewmodel.ProductDetailsVM
import com.google.accompanist.coil.rememberCoilPainter
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductDetailsScreen(id: Int, modifier: Modifier = Modifier) {
    val (state, singleEvent, processIntent) = getViewModel<ProductDetailsVM>()
    DisposableEffect("Initial") {
        // dispatch initial intent
        processIntent(DetailsViewIntent.Initial(id))
        onDispose { }
    }

    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier
    ) {
        if (state.isLoading) {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        }
        state.productDetails?.let { product ->
            Column {
                val imageSize = 72.dp
                val padding = 8.dp
                val itemHeight = imageSize + padding * 2
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Card(
                        modifier = modifier
                            .fillMaxWidth(),
                        elevation = 0.5.dp,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {


                            val painter = rememberCoilPainter(
                                request = product.image,
                                requestBuilder = { transformations(CircleCropTransformation()) },
                                previewPlaceholder = R.drawable.ic_launcher_foreground,
                            )

                            Image(
                                modifier = modifier
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(LightGray)
                                    .padding(10.dp)
                                    .width(50.dp)
                                    .height(50.dp),
                                painter = painter,

                                contentDescription = null
                            )


                            Text(
                                product.nameFa + " | " + product.nameEn,
                                style = MaterialTheme.typography.h2,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                modifier = modifier.padding(top = 10.dp),
                                color = DarkBlue
                            )

                            product.fields?.first { field -> field.name == "قیمت به دلار" }?.value?.take(
                                10
                            ).let {
                                Text(
                                    text = "$it دلار ",
                                    style = MaterialTheme.typography.h3,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center,
                                    modifier = modifier.padding(top = 10.dp),
                                    color = DarkBlue
                                )
                            }


                            var selected by remember { mutableStateOf(Price.BUY) }
                            val values = Price.values()
                            val itemCount = values.size
                            Text(
                                if (selected == Price.BUY) "خرید: ${product.buyPrice} تومان " else "فروش: ${product.sellPrice} تومان ",
                                style = MaterialTheme.typography.h1,
                                modifier = modifier.padding(top = 20.dp)
                            )

                            IconToggleButtonGroup(selected.ordinal, itemCount) { index ->
                                val align = values[index]
                                IconToggleButton(
                                    imageVector = align.imageVector,
                                    contentDescription = align.contentDescription,
                                    checked = selected == align,
                                    onCheckedChange = { selected = align },
                                )
                            }
                        }


                    }




                    state.productDetails.fields?.let {
                        DetailsList(state.productDetails.fields.slice(1..3))
                    }


                }
            }
        }

    }
}

@Composable
fun DetailsList(fields: List<Field>, modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            shape = RoundedCornerShape(6.dp),
            modifier = modifier
                .padding(12.dp)
                .fillMaxWidth(),
            elevation = 0.5.dp,
        ) {
            val lastIndex = fields.lastIndex
            LazyColumn(
                modifier = modifier

            ) {
                fields?.let {
                    itemsIndexed(fields) { index, item ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = modifier

                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            Icon(
                                imageVector = if (item.isDsc) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward,
                                contentDescription = "",
                                Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (item.isDsc) Color(
                                            225,
                                            214,
                                            212
                                        ) else Color(219, 223, 222)
                                    )
                                    .padding(12.dp),
                                tint = if (item.isDsc) Color(
                                    196,
                                    59,
                                    41
                                ) else Color(117, 162, 159)
                            )
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.h3,
                                modifier = modifier.padding(15.dp),
                                maxLines = 1,
                                color = DarkGray,
                                textAlign = TextAlign.Right,
                            )
                            Text(
                                text = if (item.isDsc) item.value.plus(" -") else item.value,
                                modifier = modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.h3,
                                maxLines = 1,
                                textAlign = TextAlign.Left,
                            )


                        }
                        if (index != lastIndex)
                            Divider(
                                modifier.padding(start = 19.dp, end = 19.dp),
                                color = com.example.myapplication.presentation.theme.Divider,
                                thickness = 0.5.dp
                            )

                    }
                }

            }
        }
    }
}

enum class Price(
    val imageVector: ImageVector,
    val contentDescription: String
) {
    BUY(Icons.Default.South, "buy"),
    SELL(Icons.Default.North , "sell"),
}

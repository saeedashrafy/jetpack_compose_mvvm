package com.example.myapplication.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.core.IntentDispatcher
import com.example.myapplication.domain.entity.Product
import com.example.myapplication.presentation.viewmodel.ProductVM
import com.example.myapplication.presentation.viewmodel.ViewIntent
import com.example.myapplication.presentation.viewmodel.ViewState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.getViewModel

@Composable
fun ProductScreen(navController: NavHostController) {

    val (state, singleEvent, processIntent) = getViewModel<ProductVM>()
    DisposableEffect("Initial") {
        // dispatch initial intent
        processIntent(ViewIntent.Initial)
        onDispose { }
    }

    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

/*    LaunchedEffect("SingleEvent") {
        // observe single event
        singleEvent
            .onEach { event ->
                Log.d("MainActivity", "handleSingleEvent $event")

                when (event) {
                    SingleEvent.Refresh.Success -> snackbarHostState.showSnackbar("Refresh success")
                    is SingleEvent.Refresh.FailureData -> snackbarHostState.showSnackbar("Refresh failure")
                    is SingleEvent.GetProductsError -> snackbarHostState.showSnackbar("Get user failure")
                }.name
            }
            .collect()
    }*/

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        ProductContent(navController, state, processIntent)
    }
}


@Composable
private fun ProductContent(
    navController: NavHostController,
    state: ViewState,
    processIntent: IntentDispatcher<ViewIntent>,
    modifier: Modifier = Modifier,
) {
    Log.e("tag", state.productItems.toString())
/*    if (state.error != null) {
        return Column(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = state.error.message ?: "An expected error",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { processIntent(ViewIntent.Retry) },
                contentPadding = PaddingValues(
                    vertical = 12.dp,
                    horizontal = 24.dp,
                ),
                shape = RoundedCornerShape(6.dp),
            ) {
                Text(text = "RETRY")
            }
        }
    }*/
    if (state.isLoading) {
        return Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
        }
    }
    ProductsList(navController, state.productItems, processIntent, state.isRefreshing)
}

@Composable
private fun ProductsList(
    navController: NavHostController,
    productItems: List<Product>,
    processIntent: IntentDispatcher<ViewIntent>,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {


    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp),
    ) {
        items(
            productItems
        ) { item ->
            ProductRow(navController, item = item)
        }
    }


}

package com.example.presentation.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.core.IntentDispatcher
import com.example.myapplication.domain.entity.News
import com.example.myapplication.domain.entity.Product
import com.example.myapplication.presentation.components.NewsRow
import com.example.myapplication.presentation.components.ProductRow
import com.example.myapplication.presentation.viewmodel.*
import com.example.myapplication.presentation.viewmodel.NewsViewIntent
import com.example.myapplication.presentation.viewmodel.ProductVM
import com.example.myapplication.presentation.viewmodel.ViewIntent
import com.example.myapplication.presentation.viewmodel.ViewState
import org.koin.androidx.compose.getViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun NewsScreen(navController: NavHostController) {


    val (state, singleEvent, processIntent) = getViewModel<NewsVM>()
    DisposableEffect("Initial") {
        // dispatch initial intent
        processIntent(NewsViewIntent.Initial)
        onDispose { }
    }

    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = Color.White
    ) {
        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        }
        NewsList(
            state.news,
            processIntent,
            state.isLoadingMore,
            state.isRefreshing,
            navController = navController
        )
    }


}

@Composable
private fun NewsList(
    news: List<News>,
    processIntent: IntentDispatcher<NewsViewIntent>,
    isLoadingMore: Boolean,
    refreshing: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val lastIndex = news.lastIndex
    LazyColumn(
        modifier = modifier

    ) {
        itemsIndexed(news) { index, item ->
            if (index == lastIndex) {
                if (!isLoadingMore)
                    processIntent(NewsViewIntent.LoadMore(index))
            }
            NewsRow(item = item, onClick = {
                navController.navigate("news_details/${item.id}") })
            /*if(isLoadingMore){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator()
                }
            }*/
        }

    }
}



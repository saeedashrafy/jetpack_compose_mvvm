package com.example.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument

import com.example.myapplication.presentation.components.ContactScreen
import com.example.myapplication.presentation.components.NewsDetailsScreen
import com.example.myapplication.presentation.components.ProductDetailsScreen
import com.example.myapplication.presentation.components.ProductScreen
import com.example.presentation.utils.Constants


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomNavigation(
        // set background color
        backgroundColor = Color.White,
        elevation = 20.dp
    )
    {
        // observe the backstack
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        // observe current route to change the icon
        // color,label color when navigated
        val currentRoute = navBackStackEntry?.destination?.route

        Constants.BottomNavItems.forEach() { navItem ->
            BottomNavigationItem(
                selected = currentRoute == navItem.route, onClick = {
                    navController.navigate(navItem.route)
                },
                icon = {
                    Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                },
                label = {
                    Text(text = navItem.label)
                },
                alwaysShowLabel = false
            )
        }
    }
}


@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "product",
        modifier = Modifier.padding(paddingValues = padding),
        builder = {

            // route: Home
            composable("product") {

                ProductScreen(navController)
            }
            // route : search
            composable("news") {
                NewsScreen(navController)
            }

            // route : profile
            composable("contact") {
                ContactScreen()
            }
            composable(
                "product_details/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            )
            { backStackEntry ->
                backStackEntry.arguments?.getInt("productId")?.let { ProductDetailsScreen(it) }
            }
            composable(
                "news_details/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                backStackEntry.arguments?.getInt("id")?.let {
                    NewsDetailsScreen(
                        id = it
                    )
                }
            }


        }
    )
}

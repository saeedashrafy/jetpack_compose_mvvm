package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.theme.MyApplicationTheme
import com.example.presentation.ui.components.BottomNavigationBar
import com.example.presentation.ui.components.NavHostContainer
import com.example.presentation.ui.components.Toolbar
import com.example.presentation.utils.Constants
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                // remember navController so it does not
                // get recreated on recomposition
                val navController = rememberNavController()
                var canPop by remember { mutableStateOf(false) }
                navController.addOnDestinationChangedListener { controller, _, _ ->
                    canPop= !Constants.BottomNavItems.map { it.route }.contains(controller.currentBackStackEntry?.destination?.route)

                   // canPop =   controller.previousBackStackEntry != null
                }
                val navigationIcon: (@Composable () -> Unit)? =
                    if (canPop) {
                        {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = null
                                )
                            }
                        }
                    } else {
                        null
                    }
                // Remember a SystemUiController
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        // Bottom navigation

                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        },
                        //Toolbar
                        topBar = {

                            Toolbar(navigationIcon,{})
                        },
                        //Content
                        content = { padding ->
                            // Navhost: where screens are placed
                            NavHostContainer(navController = navController, padding = padding)
                        })
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}
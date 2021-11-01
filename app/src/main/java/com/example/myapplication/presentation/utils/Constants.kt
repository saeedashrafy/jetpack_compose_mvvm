package com.example.presentation.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import com.example.myapplication.presentation.components.BottomNavItem


object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "درخواست",
            icon = Icons.Filled.StickyNote2,
            route = "contact"
        ),
        BottomNavItem(
            label = "اخبار",
            icon = Icons.Filled.Reorder,
            route = "news"
        ),
        BottomNavItem(
            label = "بازار",
            icon = Icons.Filled.LocalAtm,
            route = "product"
        )
    )
}
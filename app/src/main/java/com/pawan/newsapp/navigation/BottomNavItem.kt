package com.pawan.newsapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val route: String, val icon: ImageVector) {
    object News : BottomNavItem("News", "home", Icons.Filled.Home)
    object Discover : BottomNavItem("Discover", "news", Icons.Filled.Search)
    object Favorite : BottomNavItem("Favorite", "favorites", Icons.Filled.Favorite)
    object Settings : BottomNavItem("Settings", "settings", Icons.Filled.Settings)
}

package com.pawan.newsapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.pawan.newsapp.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItem.News.route) {
        composable(BottomNavItem.News.route) { NewsScreen(navController = navController) }

        composable(BottomNavItem.Discover.route) { DiscoverScreen(navController) }

        composable(BottomNavItem.Favorite.route) { FavoriteScreen() }

        composable(BottomNavItem.Settings.route) { SettingsScreen() }

        composable(
            "detail/{title}/{description}/{imageUrl}/{url}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("imageUrl") { type = NavType.StringType },
                navArgument("url") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "No Title"
            val description = backStackEntry.arguments?.getString("description") ?: "No Description"
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
            val url = backStackEntry.arguments?.getString("url") ?: ""
            DetailScreen(navController, title, description, imageUrl, url)
        }
    }
}

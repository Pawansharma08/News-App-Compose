package com.pawan.newsapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.pawan.newsapp.screens.*
import com.pawan.newsapp.viewmodel.FavoritesViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    // Initialize the ViewModel once for the entire navigation graph
    val favoritesViewModel: FavoritesViewModel = viewModel()

    NavHost(navController = navController, startDestination = BottomNavItem.News.route) {
        // News screen
        composable(BottomNavItem.News.route) {
            NewsScreen(navController = navController)
        }

        // Discover screen
        composable(BottomNavItem.Discover.route) {
            DiscoverScreen(navController = navController)
        }

        // Favorites screen
        composable(BottomNavItem.Favorite.route) {
            FavoritesScreen(favoritesViewModel = favoritesViewModel,navController)
        }

        // Settings screen
        composable(BottomNavItem.Settings.route) {
            SettingsScreen()
        }

        // Detail screen with arguments (title, description, imageUrl, url)
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

            // Pass the favoritesViewModel to DetailScreen
            DetailScreen(navController, title, description, imageUrl, url, favoritesViewModel)
        }
    }
}

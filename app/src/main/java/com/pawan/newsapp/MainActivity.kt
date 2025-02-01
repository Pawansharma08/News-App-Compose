package com.pawan.newsapp

import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pawan.newsapp.navigation.AppNavigation
import com.pawan.newsapp.navigation.BottomNavigationBar
import com.pawan.newsapp.ui.theme.NewsAppTheme
import com.pawan.newsapp.viewmodel.SplashViewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { !viewModel.isReady.value }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_X, 0.4f, 0.0f)
                val zoomY = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_Y, 0.4f, 0.0f)

                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()
                zoomX.duration = 500L
                zoomY.duration = 500L
                zoomX.doOnEnd { screen.remove() }
                zoomY.doOnEnd { screen.remove() }

                zoomX.start()
                zoomY.start()
            }
        }
        setContent {
            NewsAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val showBottomBar = when (navBackStackEntry?.destination?.route) {
        "home", "favorites", "settings" -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            AppNavigation(navController = navController)
        }
    }
}


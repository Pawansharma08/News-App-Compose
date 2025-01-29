package com.pawan.newsapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.pawan.newsapp.navigation.AppNavigation
import com.pawan.newsapp.screens.NewsScreen
import com.pawan.newsapp.ui.theme.NewsAppTheme
import com.pawan.newsapp.viewmodel.SplashViewModel


class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                !viewModel.isReady.value
            }
            setOnExitAnimationListener{ screen ->
                val zoomx  = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.4f,
                    0.0f
                )
                zoomx.interpolator = OvershootInterpolator()
                zoomx.duration = 500L
                zoomx.doOnEnd { screen.remove() }

                val zoomY  = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.4f,
                    0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 500L
                zoomY.doOnEnd { screen.remove() }

                zoomx.start()
                zoomY.start()

            }
        }
        setContent {
            NewsAppTheme  {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MyApp()
                }
            }
        }
    }
}


@Composable
fun MyApp() {
    MaterialTheme {
        Surface {
            val navController = rememberNavController()
            AppNavigation(navController = navController)
        }
    }
}


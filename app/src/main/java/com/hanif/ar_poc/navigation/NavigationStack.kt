package com.hanif.ar_poc.navigation// NavigationStack.kt
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hanif.ar_poc.SelectionScreen
import com.hanif.ar_poc.distance.DistanceScreen
import com.hanif.ar_poc.screenshotandvideorecording.ScreenshotAndVideoRecording

@Composable
fun NavigationStack() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.SelectionScreen.route) {

        composable(route = Screen.SelectionScreen.route) {
            SelectionScreen { route ->
                navController.navigate(route)
            }
        }

        composable(route = Screen.ScreenshotAndVideo.route) {
            ScreenshotAndVideoRecording()
        }

        composable(route = Screen.Distance.route) {
            DistanceScreen()
        }
    }
}
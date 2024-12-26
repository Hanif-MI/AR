package com.hanif.ar_poc.navigation

// Screen.kt
sealed class Screen(val route: String) {
    object SelectionScreen: Screen("selection_screen")
    object ScreenshotAndVideo: Screen("screenshot_and_video_screen")
    object Distance: Screen("distance_screen")
}
package com.hanif.ar_poc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hanif.ar_poc.navigation.Screen


/* Created by Hanif on 25/12/24 */

@Composable
fun SelectionScreen(onClick: (String) -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { onClick.invoke(Screen.ScreenshotAndVideo.route) }) {
            Text("ScreenShot & Video ")
        }

        Button(onClick = { onClick.invoke(Screen.Distance.route) }) {
            Text("Distance")
        }
    }
}
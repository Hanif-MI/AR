package com.hanif.ar_poc.helpers

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import io.github.sceneview.ar.ARSceneView


const val IMAGE_CAPTURE_HANDLER_NAME = "PixelCopier"

/**
 * Capture image from [ARSceneView] using [PixelCopy].
 *
 * @return Returns the [Bitmap] of view.
 */
fun ARSceneView.captureImage(): Bitmap {
    // Create a bitmap the size of the scene view.
    val bitmap = Bitmap.createBitmap(
        width, height,
        Bitmap.Config.ARGB_8888
    )

    // Create a handler thread to offload the processing of the image.
    val handlerThread = HandlerThread(IMAGE_CAPTURE_HANDLER_NAME)
    handlerThread.start()

    PixelCopy.request(this, bitmap, { copyResult ->
        if (copyResult == PixelCopy.SUCCESS) {
            print("Created bitmap from ARSceneView success.")
        } else {
            print("Failed to create bitmap from ARSceneView.")
        }
        handlerThread.quitSafely()
    }, Handler(handlerThread.looper))

    return bitmap
}
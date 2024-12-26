package com.hanif.ar_poc.distance

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Pose
import com.google.ar.core.TrackingFailureReason
import com.hanif.ar_poc.R
import com.hanif.ar_poc.screenshotandvideorecording.createAnchorNode
import io.github.sceneview.SceneView
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.collision.Quaternion
import io.github.sceneview.collision.Vector3
import io.github.sceneview.math.toFloat3
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.pow
import kotlin.math.sqrt


@Composable
fun DistanceScreen() {
    var arSceneView by remember { mutableStateOf<ARSceneView?>(null) }
    var calculatedDistance by remember { mutableStateOf<String?>(null) }

    Box(contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            val context = LocalContext.current
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val materialLoader = rememberMaterialLoader(engine)
            val cameraNode = rememberARCameraNode(engine)
            val childNodes = rememberNodes()
            val view = rememberView(engine)
            val collisionSystem = rememberCollisionSystem(view)

            var planeRenderer by remember { mutableStateOf(true) }
            val modelInstances = remember { mutableListOf<ModelInstance>() }
            var trackingFailureReason by remember { mutableStateOf<TrackingFailureReason?>(null) }
            var frame by remember { mutableStateOf<Frame?>(null) }

            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                collisionSystem = collisionSystem,
                onViewCreated = { arSceneView = this },
                onViewUpdated = { arSceneView = this },
                sessionConfiguration = { session, config ->
                    config.depthMode =
                        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                            Config.DepthMode.AUTOMATIC
                        } else {
                            Config.DepthMode.DISABLED
                        }
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                cameraNode = cameraNode,
                planeRenderer = planeRenderer,
                onTrackingFailureChanged = { trackingFailureReason = it },
                onSessionUpdated = { _, updatedFrame ->
                    frame = updatedFrame
                },
                onGestureListener = rememberOnGestureListener(
                    onSingleTapConfirmed = { motionEvent, node ->
                        if (node == null) {
                            val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                            val hitResult = hitResults?.firstOrNull {
                                it.isValid(depthPoint = true, point = true)
                            }
                            when (childNodes.size) {
                                0 -> {
                                    hitResult?.createAnchorOrNull()?.let { anchor ->
                                        planeRenderer = false
                                        childNodes += createAnchorNode(
                                            engine = engine,
                                            modelLoader = modelLoader,
                                            materialLoader = materialLoader,
                                            modelInstances = modelInstances,
                                            anchor = anchor,
                                            kModelFile = "models/pin.glb"
                                        )
                                    }
                                }

                                1 -> {
                                    hitResult?.createAnchorOrNull()?.let { anchor ->
                                        planeRenderer = false
                                        childNodes += createAnchorNode(
                                            engine = engine,
                                            modelLoader = modelLoader,
                                            materialLoader = materialLoader,
                                            modelInstances = modelInstances,
                                            anchor = anchor,
                                            kModelFile = "models/pin.glb"
                                        )
                                    }

                                    val position1 = childNodes[0].worldPosition
                                    val position2 = childNodes[1].worldPosition

                                    val distance = calculateDistance(
                                        position1.x - position2.x,
                                        position1.y - position2.y,
                                        position1.z - position2.z
                                    )

                                    calculatedDistance =
                                        "Distance: ${changeUnit(distance, "cm")} cm"
                                }

                                else -> {
                                    childNodes.clear()
                                    hitResult?.createAnchorOrNull()?.let { anchor ->
                                        planeRenderer = false
                                        childNodes += createAnchorNode(
                                            engine = engine,
                                            modelLoader = modelLoader,
                                            materialLoader = materialLoader,
                                            modelInstances = modelInstances,
                                            anchor = anchor,
                                            kModelFile = "models/pin.glb"
                                        )
                                    }
                                    calculatedDistance = null
                                }
                            }
                        } else {
                            Toast.makeText(context, "", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            )

            // Update the distance dynamically
            LaunchedEffect(childNodes) {
                while (true) {
                    if (childNodes.size == 2) {
                        val position1 = childNodes[0].worldPosition
                        val position2 = childNodes[1].worldPosition

                        val distance = calculateDistance(
                            position1.x - position2.x,
                            position1.y - position2.y,
                            position1.z - position2.z
                        )

                        calculatedDistance = "Distance: ${changeUnit(distance, "cm")} cm"
                    }
                    delay(100) // Update every 100ms
                }
            }

            Text(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 32.dp, end = 32.dp),
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                color = Color.White,
                text = trackingFailureReason?.getDescription(LocalContext.current)
                    ?: if (childNodes.isEmpty()) {
                        stringResource(R.string.point_your_phone_down)
                    } else {
                        stringResource(R.string.tap_anywhere_to_add_model)
                    }
            )

            calculatedDistance?.let {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    text = it,
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun calculateDistance(x: Float, y: Float, z: Float): Float {
    return sqrt(x.pow(2) + y.pow(2) + z.pow(2))
}

private fun changeUnit(distanceMeter: Float, unit: String): Float {
    return when (unit) {
        "cm" -> distanceMeter * 100
        "mm" -> distanceMeter * 1000
        else -> distanceMeter
    }
}

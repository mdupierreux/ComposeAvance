package be.maximedupierreux.composeavance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.maximedupierreux.composeavance.ui.theme.ComposeAvanceTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeAvanceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowAnimatable()
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ShowAnimatedContent() {
    Row {
        var count by remember { mutableStateOf(0) }
        Button(onClick = { count++ }) {
            Text("Add")
        }
        AnimatedContent(targetState = count) { targetCount ->
            // Make sure to use `targetCount`, not `count`.
            Text(text = "Count: $targetCount")
        }
    }
}

@Composable
fun ShowAnimatedContentSize() {
    var isLoading by remember {
        mutableStateOf(false)
    }
    Button(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        onClick = { isLoading = !isLoading }
    ) {
        Text(
            text = if (isLoading) {
                "Content loading"
            } else {
                "Load"
            },
            modifier = Modifier.animateContentSize()
        )
    }
}

@Composable
fun AnimateAsState() {
    val enabled = remember { mutableStateOf(true) }
    val alpha: Float by animateFloatAsState(if (enabled.value) 1f else 0.1f)
    Column(
        Modifier.fillMaxSize()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .graphicsLayer(alpha = alpha)
                .background(Color.Red)
        )
        Button(onClick = { enabled.value = !enabled.value } ) {
           Text(text = "Animate")
        }
    }
}

@Composable
fun AnimateVisibility() {
    var visible by remember { mutableStateOf(true) }
    val density = LocalDensity.current
    Column(
        Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically {
                // Slide in from 40 dp from the top.
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                // Expand from the top.
                expandFrom = Alignment.Top
            ) + fadeIn(
                // Fade in with the initial alpha of 0.3f.
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Text("Hello",
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Red))
        }
        Button(onClick = { visible = !visible } ) {
            Text(text = "Animate")
        }
    }
}

@Composable
fun ShowCrossfade() {
    var currentPage by remember { mutableStateOf("A") }
    Column(
        Modifier.fillMaxSize()
    ) {
        Crossfade(targetState = currentPage) { screen ->
            when (screen) {
                "A" ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.Blue)
                    ) {
                        Text("Page A", fontSize = 24.sp)
                    }
                "B" ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.Red)
                    ) {
                        Text("Page B", fontSize = 24.sp)
                    }
            }
        }
        Button(onClick = { currentPage = if (currentPage == "A") "B" else "A" }) {
            Text(text = "Change page")
        }
    }
}

@Composable
fun ShowAnimatable() {
    val color = remember { Animatable(Color.Gray) }
    var ok by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(ok) {
        color.animateTo(
            when (ok) {
                true -> Color.Green
                false -> Color.Red
                else -> Color.Gray
            })
    }
    Column(Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(color.value))
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = { ok = true }) {
                Text(text = "Ok")
            }
            Button(onClick = { ok = false }) {
                Text(text = "Not ok")
            }
        }
    }
}


@Composable
fun Keyframes() {
    var enabled by remember { mutableStateOf(true) }

    val value by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.1f,
        animationSpec = keyframes {
            durationMillis = 375
            0.0f at 0 with LinearOutSlowInEasing // for 0-15 ms
            0.2f at 15 with FastOutLinearInEasing // for 15-75 ms
            0.4f at 75 // ms
            0.4f at 225 // ms
        }
    )
    Column {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .graphicsLayer(value)
            .background(Color.Red)
        )

        Button (onClick = { enabled = !enabled }) {
            Text(text = "Toggle")
        }
    }

}


@Composable
fun ShowRepeatable() {
    var enabled by remember { mutableStateOf(true) }

    val value by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.1f,
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(durationMillis = 300),
            repeatMode = RepeatMode.Reverse
        )
    )
    Column {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .graphicsLayer(value)
            .background(Color.Red)
        )

        Button (onClick = { enabled = !enabled }) {
            Text(text = "Toggle")
        }
    }
}


@Composable
fun ShowInfiniteRepeatable() {
    var enabled by remember { mutableStateOf(true) }

    val value by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300),
            repeatMode = RepeatMode.Reverse
        )
    )
    Column {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .graphicsLayer(value)
            .background(Color.Red)
        )

        Button (onClick = { enabled = !enabled }) {
            Text(text = "Toggle")
        }
    }
}



@Composable
fun ShowSnap() {
    var enabled by remember { mutableStateOf(true) }

    val value by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.1f,
        animationSpec = snap(300)
    )
    Column {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .graphicsLayer(value)
            .background(Color.Red)
        )

        Button (onClick = { enabled = !enabled }) {
            Text(text = "Toggle")
        }
    }
}

fun Modifier.tiltOnTouch(
    maxTiltDegrees: Float = DEF_MAX_TILT_DEGREES
) = this.then(
    composed {
        val scope = rememberCoroutineScope()
        val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

        pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    var newOffset = awaitFirstDown().position.normalize(size)
                    scope.launch {
                        offset.animateTo(newOffset, spring)
                    }
                    do {
                        val event = awaitPointerEvent()
                        newOffset = event.changes.last().position.normalize(size)
                        scope.launch {
                            offset.animateTo(newOffset, spring)
                        }
                    } while (event.changes.none { it.changedToUp() })
                    scope.launch {
                        offset.animateTo(Offset.Zero, releaseSpring)
                    }
                }
            }
        }.tilt(
            offset = offset.value,
            maxTiltDegrees = maxTiltDegrees
        )
    }
)

private val spring = spring<Offset>(stiffness = Spring.StiffnessMediumLow)
private val releaseSpring = spring<Offset>(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = 300f)

private fun Offset.normalize(size: IntSize) = Offset(
    ((x - (size.width / 2)) / (size.width / 2)).coerceIn(-1f..1f),
    ((y - (size.height / 2)) / (size.height / 2)).coerceIn(-1f..1f)
)

fun Modifier.tilt(
    offset: Offset = Offset.Zero,
    maxTiltDegrees: Float = DEF_MAX_TILT_DEGREES
) = this.graphicsLayer(
    rotationY = offset.x * maxTiltDegrees,
    rotationX = -offset.y * maxTiltDegrees,
    cameraDistance = 20f
)

const val DEF_MAX_TILT_DEGREES = 15f
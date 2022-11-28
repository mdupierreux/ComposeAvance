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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.maximedupierreux.composeavance.ui.theme.ComposeAvanceTheme

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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeAvanceTheme {
        AnimateAsState()
    }
}
package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun ZannLauncherSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startExitAnimation by remember { mutableStateOf(false) }
    
    // Animatable states for entry animations
    val logoScale = remember { Animatable(0.4f) }
    val logoAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(50f) }
    val textAlpha = remember { Animatable(0f) }
    
    // Overall screen fade/exit animation
    val screenAlpha by animateFloatAsState(
        targetValue = if (startExitAnimation) 0f else 1f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "screenAlpha"
    )

    // Infinite transitions for rotating orbits and pulsing auras
    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    
    val auraScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auraScale1"
    )
    val auraAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auraAlpha1"
    )

    val auraScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, delayMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auraScale2"
    )
    val auraAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, delayMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auraAlpha2"
    )

    // Orbits rotation
    val orbitRotation1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitRotation1"
    )
    val orbitRotation2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitRotation2"
    )

    // Dynamic intelligent boot logs
    val statusMessages = remember {
        listOf(
            "Menginisialisasi inti sistem Zann AI...",
            "Mengonfigurasi simpul sensorik...",
            "Sinkronisasi matriks memori...",
            "Mengoptimalkan performa kognitif...",
            "Zann AI siap digunakan!"
        )
    }
    var currentStatusIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        // Trigger animations in parallel
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
        launch {
            delay(300)
            textOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        launch {
            delay(300)
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 900)
            )
        }

        // Rolling status message updates
        launch {
            while (currentStatusIndex < statusMessages.size - 1) {
                delay(500)
                currentStatusIndex++
            }
        }

        // Active display duration for the splash screen
        delay(2500)
        
        // Begin elegant transition out
        startExitAnimation = true
        delay(600)
        onFinished()
    }

    // Determine background color/gradients based on system theme
    val isDark = isSystemInDarkTheme()
    val bgGradient = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF070A13), // Deep spaces black-blue
                Color(0xFF030408)  // Ultra dark
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF1F5F9), // Slate 100
                Color(0xFFE2E8F0)  // Slate 200
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
            .graphicsLayer { alpha = screenAlpha },
        contentAlignment = Alignment.Center
    ) {
        // Aesthetic layered background radial glows
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.Center)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (isDark) Color(0x1B6366F1) else Color(0x0F6366F1), // Soft Indigo aura
                            Color.Transparent
                        ),
                        radius = 500.dp.value
                    )
                )
        )

        // Subtle background neural node synapses
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(0.5.dp)
        ) {
            val nodes = listOf(
                Pair(0.12f, 0.22f),
                Pair(0.88f, 0.18f),
                Pair(0.25f, 0.78f),
                Pair(0.78f, 0.82f),
                Pair(0.08f, 0.58f),
                Pair(0.92f, 0.52f),
                Pair(0.35f, 0.15f),
                Pair(0.65f, 0.88f)
            )
            val baseColor = if (isDark) Color(0xFF6366F1) else Color(0xAA6366F1)
            
            nodes.forEach { (x, y) ->
                val px = x * size.width
                val py = y * size.height
                
                // Continuous smooth pulsating wave using unique positions for phase offsets
                val pulse = (sin((System.currentTimeMillis() / 450.0) + (x * 10f) + (y * 10f)) + 1.0) / 2.0
                val radius = (2.dp.toPx() + (3.dp.toPx() * pulse)).toFloat()
                val alphaVal = (0.05f + (0.28f * pulse)).toFloat()
                
                drawCircle(
                    color = baseColor.copy(alpha = alphaVal),
                    radius = radius,
                    center = androidx.compose.ui.geometry.Offset(px, py)
                )
                drawCircle(
                    color = baseColor.copy(alpha = alphaVal * 0.3f),
                    radius = radius * 2.5f,
                    center = androidx.compose.ui.geometry.Offset(px, py)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated logo section with glowing aura & digital orbits
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        alpha = logoAlpha.value
                    }
            ) {
                // Outer complex rotating orbital lines
                val orbitColor1 = if (isDark) Color(0xFF22D3EE) else Color(0xFF0891B2) // Cyan
                val orbitColor2 = if (isDark) Color(0xFF818CF8) else Color(0xFF4F46E5) // Indigo
                
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius1 = 78.dp.toPx()
                    val radius2 = 64.dp.toPx()
                    
                    // Rotating outer dashed orbital (Clockwise)
                    rotate(orbitRotation1) {
                        drawCircle(
                            color = orbitColor1.copy(alpha = 0.35f),
                            radius = radius1,
                            style = Stroke(
                                width = 1.2f.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                            )
                        )
                    }
                    
                    // Rotating inner dashed orbital (Counter-Clockwise)
                    rotate(orbitRotation2) {
                        drawCircle(
                            color = orbitColor2.copy(alpha = 0.3f),
                            radius = radius2,
                            style = Stroke(
                                width = 1f.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 15f), 0f)
                            )
                        )
                    }
                }

                // Pulsing outer aura ring 2
                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .scale(auraScale2)
                        .graphicsLayer { alpha = auraAlpha2 }
                        .border(
                            border = BorderStroke(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            ),
                            shape = CircleShape
                        )
                )

                // Pulsing outer aura ring 1
                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .scale(auraScale1)
                        .graphicsLayer { alpha = auraAlpha1 }
                        .border(
                            border = BorderStroke(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            ),
                            shape = CircleShape
                        )
                )

                // Main Logo Card with elegant dynamic shadow
                Card(
                    modifier = Modifier
                        .size(105.dp)
                        .border(
                            border = BorderStroke(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                            ),
                            shape = CircleShape
                        ),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF0F172A) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.zann_ai_z_logo_1781975518951),
                            contentDescription = "Logo Zann AI",
                            modifier = Modifier
                                .size(95.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Branding text section with spring-based translation and opacity rise
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    translationY = textOffsetY.value
                    alpha = textAlpha.value
                }
            ) {
                Text(
                    text = "Zann AI",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Advanced rolling log status
                Text(
                    text = statusMessages[currentStatusIndex],
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .height(24.sp.value.dp) // Maintain space to prevent layout shifting
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                )
                
                Spacer(modifier = Modifier.height(44.dp))
                
                // Sleek pulsing futuristic progress bar indicator
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ) {
                    val progressTransition = rememberInfiniteTransition(label = "indicator")
                    val progressOffset by progressTransition.animateFloat(
                        initialValue = -1f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "progressOffset"
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.35f)
                            .fillMaxHeight()
                            .align(Alignment.CenterStart)
                            .graphicsLayer {
                                translationX = (140.dp.value * progressOffset)
                            }
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                        if (isDark) Color(0xFF22D3EE) else MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )
                }
            }
        }
        
        // Subtle version number in footer
        Text(
            text = "v2.2.0-Alpha AI",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .graphicsLayer { alpha = textAlpha.value }
        )
    }
}

package com.cocos.glimmer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
// import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.geometry.CornerRadius
// import androidx.compose.ui.geometry.Offset
// import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.random.Random

val DeepSeaStart = Color(0xFF0D1b2A)
val DeepSeaEnd = Color(0xFF1B263B)
val GlimmerGold = Color(0xFFFFD700)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val viewModel: OceanViewModel = viewModel()
                OceanScreen(viewModel)
            }
        }
    }
}

@Composable
fun OceanScreen(viewModel: OceanViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showWriteDialog by remember { mutableStateOf(false) }
    var pickedBottle by remember { mutableStateOf<Bottle?>(null) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepSeaStart, DeepSeaEnd)))
    ) {
        uiState.bottles.forEach { bottle ->
            key(bottle.id) {
                DriftingBottleNode(bottle) {
                    if (uiState.dailyPicksLeft > 0) {
                        viewModel.tryToPickBottle(bottle.id)
                        pickedBottle = bottle
                    } else {
                        viewModel.tryToPickBottle(bottle.id)
                    }
                }
            }
        }

        Text(
            text = "今日剩余捡拾次数：${uiState.dailyPicksLeft}/5",
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = 48.dp),
            style = MaterialTheme.typography.labelLarge
        )

        FloatingActionButton(
            onClick = { showWriteDialog = true },
            containerColor = GlimmerGold,
            contentColor = DeepSeaStart,
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(32.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Throw")
        }

        if (showWriteDialog) {
            WriteDialog(
                onDismiss = { showWriteDialog = false },
                onSend = { content ->
                    viewModel.throwBottle(content)
                    showWriteDialog = false
                }
            )
        }

        pickedBottle?.let { bottle ->
            ReadDialog(
                bottle = bottle,
                onDismiss = { pickedBottle = null }
            )
        }
    }
}

@Composable
fun DriftingBottleNode(bottle: Bottle, onClick: () -> Unit) {
    val randomX = remember { Random.nextFloat() }
    val randomY = remember { Random.nextFloat() }

    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + Random.nextInt(1000), easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Box(
        modifier = Modifier.fillMaxSize()
            .wrapContentSize(align = Alignment.TopStart)
            .graphicsLayer {
                val screenWidth = this.size.width
                val screenHeight = this.size.height
                translationX = 0.1f*screenWidth + 0.8f*screenWidth*randomX
                translationY = 0.1f*screenHeight + 0.8f*screenHeight*randomY + offsetY
            }
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(12.dp)
                .background(Color(bottle.moodColor), CircleShape)
                .blur(8.dp)
        )
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color(bottle.moodColor).copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun WriteDialog(onDismiss: () -> Unit, onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("写下你的心情") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length < 100) text = it },
                label = { Text("最多100字") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { if (text.isNotBlank()) onSend(text) }) {
                Text("放入大海")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
fun ReadDialog(bottle: Bottle, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepSeaStart.copy(alpha = 0.9f),
        title = { Text("来自远方的微光", color = GlimmerGold) },
        text = {
            Column {
                Text(text = bottle.content, color = Color.White, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "—— ${bottle.senderName}", color = Color.Gray, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults
                    .buttonColors(containerColor = GlimmerGold, contentColor = DeepSeaStart)
            ) {
                Text("收下温暖")
            }
        }
    )
}

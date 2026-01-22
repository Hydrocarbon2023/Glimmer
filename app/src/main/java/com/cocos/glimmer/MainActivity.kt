package com.cocos.glimmer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cocos.glimmer.ui.theme.CoralPink
import com.cocos.glimmer.ui.theme.DeepSeaEnd
import com.cocos.glimmer.ui.theme.DeepSeaStart
import com.cocos.glimmer.ui.theme.GlimmerGold
import com.cocos.glimmer.ui.theme.OceanGray
import com.cocos.glimmer.ui.theme.SeaFoamWhite
import com.cocos.glimmer.ui.theme.SunSandYellow
import com.cocos.glimmer.ui.theme.TranslucentBg
import com.cocos.glimmer.ui.theme.TropicalSeaEnd
import com.cocos.glimmer.ui.theme.TropicalSeaStart
import kotlin.random.Random

@Composable
fun BottleIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawRoundRect(
            color = color,
            topLeft = Offset(w*0.1f, h*0.2f),
            size = Size(w*0.8f, h*0.8f),
            cornerRadius = CornerRadius(w*0.15f, w*0.15f)
        )

        drawRoundRect(
            color = color,
            topLeft = Offset(w*0.15f, 0f),
            size = Size(w*0.7f, h*0.18f),
            cornerRadius = CornerRadius(w*0.05f, w*0.05f)
        )

        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(w*0.25f, h*0.3f),
            end = Offset(w*0.25f, h*0.85f),
            strokeWidth = w*0.08f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                val viewModel: OceanViewModel = viewModel()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(navController)
                    }

                    composable("register") {
                        RegisterScreen(navController)
                    }

                    composable("ocean") {
                        OceanScreen(viewModel, navController)
                    }

                    composable("notifications") {
                        NotificationScreen(navController)
                    }

                    composable(
                        route = "chat/{bottleId}",
                        arguments = listOf(navArgument("bottleId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val bottleId = backStackEntry
                            .arguments?.getString("bottleId") ?: return@composable
                        ChatScreen(navController, bottleId)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OceanScreen(viewModel: OceanViewModel, navController: NavController) {
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

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(TropicalSeaEnd, TropicalSeaStart)))
    ) {
        val maxWidth = maxWidth
        val maxHeight = maxHeight

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 48.dp, end = 24.dp)
                .zIndex(1f),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { navController.navigate("notifications") }) {
                if (SimulationDB.notifications.isNotEmpty()) {
                    BadgedBox(
                        badge = { Badge { Text(SimulationDB.notifications.size.toString()) } }
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Alerts",
                            tint = GlimmerGold,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Alerts",
                        tint = GlimmerGold,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        uiState.bottles.forEach { bottle ->
            key(bottle.id) {
                DriftingBottleNode(
                    bottle = bottle,
                    parentWidth = maxWidth.value,
                    parentHeight = maxHeight.value,
                    onClick = {
                        if (uiState.dailyPicksLeft > 0) {
                            viewModel.tryToPickBottle(bottle.id)
                            pickedBottle = bottle
                        } else {
                            viewModel.tryToPickBottle(bottle.id)
                        }
                    }
                )
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
                navController = navController,
                onDismiss = { pickedBottle = null }
            )
        }
    }
}

@Composable
fun DriftingBottleNode(
    bottle: Bottle,
    parentWidth: Float,
    parentHeight: Float,
    onClick: () -> Unit
) {
    val randomX = remember { Random.nextFloat()*parentWidth*0.8f + parentWidth*0.1f }
    val randomY = remember { Random.nextFloat()*parentHeight*0.8f + parentHeight*0.1f }

    val randomWidth = remember { Random.nextInt(20, 45).dp }
    val randomHeight = randomWidth*1.6f

    val randomRotation = remember { Random.nextFloat()*40f - 20f }

    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + Random.nextInt(1000), easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Box(
        modifier = Modifier.offset(x = randomX.dp, y = randomY.dp + offsetY.dp)
            .width(randomWidth)
            .height(randomHeight)
            .graphicsLayer {
                rotationZ = randomRotation
            }
            .clickable { onClick() }
    ) {
        BottleIcon(
            color = Color(bottle.moodColor),
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier.align(Alignment.Center)
                .align(Alignment.Center)
                .width(randomWidth*0.6f)
                .height(randomHeight*0.6f)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f), CircleShape)
                .blur(8.dp)
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
fun ReadDialog(bottle: Bottle, navController: NavController, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TranslucentBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "来自远方的微光",
                    color = GlimmerGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = CoralPink,
                    modifier = Modifier.size(16.dp)
                )
                Text(text = " ${bottle.likes}", color = CoralPink, fontSize = 14.sp)
            }
        },
        text = {
            Column {
                Text(
                    text = bottle.content,
                    color = SeaFoamWhite,
                    fontSize = 17.sp,
                    lineHeight = 26.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "—— ${bottle.senderName}",
                    color = OceanGray,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        SimulationDB.likeBottle(bottle)
                    },
                    modifier = Modifier.size(48.dp)
                        .background(Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = CoralPink
                    )
                }

                Button(
                    onClick = {
                        onDismiss()
                        navController.navigate("chat/${bottle.id}")
                    },
                    colors = ButtonDefaults
                        .buttonColors(containerColor = GlimmerGold, contentColor = DeepSeaStart),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("回复", fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults
                        .buttonColors(containerColor = OceanGray, contentColor = DeepSeaStart)
                ) {
                    Text("收下温暖", color = SeaFoamWhite.copy(0.7f))
                }
            }
        }
    )
}

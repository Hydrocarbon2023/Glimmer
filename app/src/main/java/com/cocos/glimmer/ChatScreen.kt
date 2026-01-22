package com.cocos.glimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cocos.glimmer.ui.theme.DeepSeaEnd
import com.cocos.glimmer.ui.theme.DeepSeaStart
import com.cocos.glimmer.ui.theme.GlimmerGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, bottleId: String) {
    val chatHistory = SimulationDB.getChatHistory(bottleId)

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(DeepSeaStart)
    ) {
        TopAppBar(
            title = { Text("漂流对话", color = GlimmerGold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepSeaEnd)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(chatHistory) {msg ->
                ChatBubble(message = msg)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .background(DeepSeaEnd)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("回复", color = Color.Gray) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = GlimmerGold,
                    unfocusedBorderColor = Color.White.copy(0.3f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        SimulationDB.sendReply(
                            bottleId,
                            inputText,
                            AuthManager.currentUser ?: "我"
                        )
                        inputText = ""
                    }
                },
                modifier = Modifier.background(GlimmerGold, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = DeepSeaEnd
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (message.isMe) GlimmerGold else Color.White.copy(0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                color = if(message.isMe) DeepSeaEnd else Color.White
            )
        }
        Text(
            text = message.senderName,
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    val notifications = SimulationDB.notifications

    Column(modifier = Modifier.fillMaxSize().background(DeepSeaStart)) {
        TopAppBar(
            title = { Text("消息通知", color = GlimmerGold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepSeaEnd)
        )

        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(notifications) { note ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.1f)),
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            if (note.type == NotificationType.REPLY && note.relatedBottleId != null) {
                                navController.navigate("chat/${note.relatedBottleId}")
                            }
                        }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (note.type == NotificationType.LIKE)
                                Icons.Default.Favorite
                            else
                                Icons.Default.Email,
                            contentDescription = null,
                            tint = if (note.type == NotificationType.LIKE) Color.Red else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(note.title, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(note.content, color = Color.White.copy(0.7f), fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

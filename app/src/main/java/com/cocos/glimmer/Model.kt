package com.cocos.glimmer

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import java.util.UUID

data class Bottle(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val senderName: String = "Anonymous",
    val moodColor: Long = 0xFFFFD700,
    var likes: Int = 0,
    val isMine: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class OceanUIState(
    val bottles: List<Bottle> = emptyList(),
    val dailyPicksLeft: Int = 5,
    val isLoading: Boolean = false,
    val message: String? = null,
    val likedBottleIds: Set<String> = emptySet()
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderName: String,
    val content: String,
    val isMe: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class Notification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val relatedBottleId: String? = null,
    val type: NotificationType
)

enum class NotificationType { LIKE, REPLY }

object SimulationDB {
    private val chats = mutableStateMapOf<String, MutableList<ChatMessage>>()

    val notifications = mutableStateListOf<Notification>()

    fun likeBottle(bottle: Bottle) {
        bottle.likes++
        notifications.add(0, Notification(
            title = "收到新的喜欢❤️",
            content = "有人喜欢了你的漂流瓶：${bottle.content.take(10)}......",
            relatedBottleId = bottle.id,
            type = NotificationType.LIKE
        ))
    }

    fun sendReply(bottleId: String, content: String, senderName: String) {
        val list = chats.getOrPut(bottleId) { mutableListOf() }

        list.add(ChatMessage(senderName = senderName, content = content, isMe = true))

        notifications.add(0, Notification(
            title = "收到新回复💬",
            content = "有人回复了你：${content.take(10)}......",
            relatedBottleId = bottleId,
            type = NotificationType.REPLY
        ))

        // 模拟对方回复，正式版中需删去
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            list.add(ChatMessage(senderName = "对方", content = "收到！英雄所见略同", isMe = false))
        }, 1000)
    }

    fun getChatHistory(bottleId: String): List<ChatMessage> {
        return chats[bottleId] ?: emptyList()
    }
}

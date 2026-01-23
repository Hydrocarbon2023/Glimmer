package com.cocos.glimmer

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OceanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OceanUIState())
    val uiState: StateFlow<OceanUIState> = _uiState.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    private val sampleMessages = listOf(
        "概率论求过！😖",
        "今天食堂的红烧肉真好吃。",
        "有没有人一起去图书馆？",
        "想去操场看星星。✨",
        "围棋社招人中！",
        "听了一首老歌，突然很想家。"
    )

    init {
        loadBottles()
    }

    private fun loadBottles() {
        _uiState.update { it.copy(isLoading = true) }

        db.collection("bottles")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _uiState.update { it.copy(message = "连接大海失败: ${e.message}", isLoading = false) }
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    if (snapshot.isEmpty) {
                        seedSampleBottles()
                    } else {
                        val bottles = snapshot.documents.mapNotNull { doc ->
                            Bottle(
                                id = doc.id,
                                content = doc.getString("content") ?: "",
                                senderName = doc.getString("senderName") ?: "Anonymous",
                                moodColor = doc.getLong("moodColor") ?: 0xFFFFD700,
                                likes = doc.getLong("likes")?.toInt() ?: 0,
                                isMine = doc.getString("senderName") == AuthManager.currentUser
                            )
                        }
                        _uiState.update { it.copy(bottles = bottles, isLoading = false) }
                    }
                }
            }
    }

    private fun seedSampleBottles() {
        sampleMessages.forEach { msg ->
            val sender = "Anonymous"

            val bottleData = hashMapOf(
                "content" to msg,
                "senderName" to sender,
                "moodColor" to 0xFFFFD700,
                "likes" to (0..5).random(),
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("bottles").add(bottleData)
        }
    }

    fun throwBottle(content: String) {
        val username = AuthManager.currentUser ?: "Anonymous"

        val bottleData = hashMapOf(
            "content" to content,
            "senderName" to username,
            "moodColor" to 0xFFFFD700,
            "likes" to 0,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("bottles").add(bottleData)
            .addOnSuccessListener {
                _uiState.update { it.copy(message = "漂流瓶已发出......") }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(message = "海浪太大，发送失败") }
            }
    }

    fun tryToPickBottle(bottleId: String) {
        val currentPicks = _uiState.value.dailyPicksLeft
        if (currentPicks > 0) {
            _uiState.update {
                it.copy(
                    dailyPicksLeft = currentPicks - 1,
                    message = "捡到了一个瓶子！🫙（今日剩余次数：${currentPicks - 1}）"
                )
            }
        } else {
            _uiState.update { it.copy(message = "不可贪心哦，明天再来吧！😊") }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun likeBottle(bottle: Bottle) {
        if (_uiState.value.likedBottleIds.contains(bottle.id)) return

        _uiState.update { state ->
            val newLiked = state.likedBottleIds + bottle.id
            state.copy(likedBottleIds = newLiked)
        }

        db.collection("bottles").document(bottle.id)
            .update("likes", FieldValue.increment(1))
            .addOnFailureListener {
                _uiState.update { it.copy(message = "点赞失败，请检查网络") }
            }
    }
}

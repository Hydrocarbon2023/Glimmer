package com.cocos.glimmer

import androidx.lifecycle.ViewModel
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OceanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OceanUIState())
    val uiState: StateFlow<OceanUIState> = _uiState.asStateFlow()

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
        val initialBottles = List(8) {
            Bottle(content = sampleMessages.random())
        }
        _uiState.update { it.copy(bottles = initialBottles) }
    }

    fun throwBottle(content: String) {
        val username = AuthManager.currentUser ?: "Anonymous"

        val newBottle = Bottle(
            content = content,
            senderName = username,
            isMine = true
        )

        _uiState.update { currentState ->
            currentState.copy(
                bottles = currentState.bottles + newBottle,
                message = "漂流瓶已发出......"
            )
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
}

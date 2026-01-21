package com.cocos.glimmer

import java.util.UUID

data class Bottle(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val senderName: String = "Anonymous",
    val moodColor: Long = 0xFFFFD700,
    val likes: Int = 0,
    val isMine: Boolean = false
)

data class OceanUIState(
    val bottles: List<Bottle> = emptyList(),
    val dailyPicksLeft: Int = 5,
    val isLoading: Boolean = false,
    val message: String? = null
)

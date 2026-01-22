package com.cocos.glimmer

object AuthManager {
    private val users = mutableMapOf<String, String>()

    fun register(username: String, password: String): Boolean {
        if (users.containsKey(username)) return false
        users[username] = password
        return true
    }

    fun login(username: String, password: String): Boolean {
        return users[username] == password
    }
}

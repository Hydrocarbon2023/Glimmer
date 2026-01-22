package com.cocos.glimmer

object AuthManager {
    private val users = mutableMapOf<String, String>()

    var currentUser: String? = null

    fun register(username: String, password: String): Boolean {
        if (users.containsKey(username)) return false
        users[username] = password
        currentUser = username
        return true
    }

    fun login(username: String, password: String): Boolean {
        val success = users[username] == password
        if (success) {
            currentUser = username
        }
        return success
    }
}

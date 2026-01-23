package com.cocos.glimmer

import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    private val auth = FirebaseAuth.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val currentUser: String?
        get() = auth.currentUser?.email?.substringBefore("@")

    fun register(username: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val email = if (username.contains("@")) username else "$username@glimmer.com"

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "注册失败😞") }
    }

    fun login(username: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val email = if (username.contains("@")) username else "$username@glimmer.com"

        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "登录失败😡") }
    }

    fun logout() {
        auth.signOut()
    }
}

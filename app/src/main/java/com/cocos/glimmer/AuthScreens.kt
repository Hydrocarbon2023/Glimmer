package com.cocos.glimmer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cocos.glimmer.ui.theme.DeepSeaEnd
import com.cocos.glimmer.ui.theme.DeepSeaStart
import com.cocos.glimmer.ui.theme.GlimmerGold

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepSeaStart, DeepSeaEnd))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "微光漂流",
                color = GlimmerGold,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Glimmer",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            GlimmerTextField(
                value = username,
                onValueChange = { username = it },
                label = "用户名",
                icon = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(16.dp))
            GlimmerTextField(
                value = password,
                onValueChange = { password = it },
                label = "密码",
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (username.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        AuthManager.login(username, password,
                            onSuccess = {
                                isLoading = false
                                navController.navigate("ocean") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onError = { errorMsg ->
                                isLoading = false
                                Toast.makeText(context, "登陆失败：$errorMsg", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GlimmerGold,
                    contentColor = DeepSeaStart,
                    disabledContainerColor = GlimmerGold.copy(0.5f),
                    disabledContentColor = DeepSeaStart.copy(0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = DeepSeaStart,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("登录", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate("register") }
            ) {
                Text("还没有账号？立即注册", color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepSeaStart, DeepSeaEnd))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "加入微光",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(48.dp))

            GlimmerTextField(
                value = username,
                onValueChange = { username = it },
                label = "请输入用户名",
                icon = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(16.dp))
            GlimmerTextField(
                value = password,
                onValueChange = { password = it },
                label = "请输入密码（至少6位）",
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (username.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        AuthManager.register(username, password,
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(context, "注册成功！请登录", Toast.LENGTH_SHORT).show()
                                navController.navigate("ocean") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            onError = { errorMsg ->
                                isLoading = false
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GlimmerGold,
                    contentColor = DeepSeaStart,
                    disabledContainerColor = GlimmerGold.copy(0.5f),
                    disabledContentColor = DeepSeaStart.copy(0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = DeepSeaStart,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("注册", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { navController.popBackStack() }) {
                Text("返回登录", color = Color.White.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun GlimmerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.7f)) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = GlimmerGold) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else
            androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GlimmerGold,
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = GlimmerGold
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

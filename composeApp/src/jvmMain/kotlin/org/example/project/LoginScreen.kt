package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (LoginResponse) -> Unit) {
    var userId by remember { mutableStateOf("CM975223") }
    var password by remember { mutableStateOf("jeffin123!") }
    var isLoading by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login to MatrimonyFinder", 
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                label = { Text("User ID") },
                modifier = Modifier.fillMaxWidth(0.5f),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(0.5f),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    isLoading = true
                    resultMessage = null
                    coroutineScope.launch {
                        val request = LoginRequest(userId = userId, password = password)
                        val result = ApiClient.login(request)
                        if (result.isSuccess) {
                            onLoginSuccess(result.getOrThrow())
                        } else {
                            resultMessage = "Error: ${result.exceptionOrNull()?.message}"
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(0.5f),
                enabled = !isLoading && userId.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), 
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login")
                }
            }
            
            resultMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it, 
                    color = if (it.startsWith("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
    }
}

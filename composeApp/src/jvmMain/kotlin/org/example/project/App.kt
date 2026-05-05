package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import java.util.prefs.Preferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val prefs = Preferences.userRoot().node("org.example.project.MatrimonyFinder")

enum class Route {
    Login, Dashboard, MyMatches, WhoViewedMe, MutualMatches, NewlyJoined
}

@Composable
@Preview
fun App() {
    var loggedInUser by remember { 
        mutableStateOf<LoginResponse?>(
            try {
                val jsonStr = prefs.get("auth_user", null)
                if (jsonStr != null) {
                    val json = Json { ignoreUnknownKeys = true }
                    json.decodeFromString<LoginResponse>(jsonStr)
                } else null
            } catch (e: Exception) {
                null
            }
        )
    }
    var currentRoute by remember { mutableStateOf(if (loggedInUser != null) Route.Dashboard else Route.Login) }

    fun handleLogout() {
        loggedInUser = null
        prefs.remove("auth_user")
        currentRoute = Route.Login
    }

    MaterialTheme {
        if (loggedInUser == null) {
            LoginScreen(onLoginSuccess = { response -> 
                loggedInUser = response 
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    // Remove large json elements to fit in Windows Registry Preferences limit (8192 bytes)
                    val slimResponse = response.copy(user = null, policyInfo = null)
                    prefs.put("auth_user", json.encodeToString(slimResponse))
                } catch (e: Exception) {
                    System.err.println("Failed to save auth_user to preferences: ${e.message}")
                    e.printStackTrace()
                }
                currentRoute = Route.Dashboard
            })
        } else {
            val token = loggedInUser!!.token ?: ""
            val candidateId = loggedInUser!!.candidates?.firstOrNull()?.id ?: ""
            
            when (currentRoute) {
                Route.Login -> {
                    currentRoute = Route.Dashboard
                }
                Route.Dashboard -> {
                    DashboardScreen(
                        onNavigateToMyMatches = { currentRoute = Route.MyMatches },
                        onNavigateToWhoViewedMe = { currentRoute = Route.WhoViewedMe },
                        onNavigateToMutualMatches = { currentRoute = Route.MutualMatches },
                        onNavigateToNewlyJoined = { currentRoute = Route.NewlyJoined },
                        onLogout = { handleLogout() }
                    )
                }
                Route.MyMatches -> {
                    MyMatchesScreen(
                        token = token, 
                        candidateId = candidateId,
                        onBack = { currentRoute = Route.Dashboard },
                        onLogout = { handleLogout() }
                    )
                }
                Route.WhoViewedMe -> {
                    WhoViewedMeScreen(
                        token = token,
                        onBack = { currentRoute = Route.Dashboard },
                        onLogout = { handleLogout() }
                    )
                }
                Route.MutualMatches -> {
                    MutualMatchesScreen(
                        token = token,
                        candidateId = candidateId,
                        onBack = { currentRoute = Route.Dashboard },
                        onLogout = { handleLogout() }
                    )
                }
                Route.NewlyJoined -> {
                    NewlyJoinedScreen(
                        token = token,
                        candidateId = candidateId,
                        onBack = { currentRoute = Route.Dashboard },
                        onLogout = { handleLogout() }
                    )
                }
            }
        }
    }
}
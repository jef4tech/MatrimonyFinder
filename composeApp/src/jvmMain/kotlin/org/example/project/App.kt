package org.example.project
import org.example.project.data.remote.models.*
import org.example.project.ui.screens.*
import org.example.project.navigation.*
import org.example.project.di.appModule

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinApplication

import java.util.prefs.Preferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val prefs = Preferences.userRoot().node("org.example.project.MatrimonyFinder")

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
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

        val navController = rememberNavController()

        fun handleLogout() {
            loggedInUser = null
            prefs.remove("auth_user")
            navController.navigate(LoginRoute) {
                popUpTo(0) { inclusive = true }
            }
        }

        MaterialTheme {
            val startDestination = if (loggedInUser != null) DashboardRoute else LoginRoute

            NavHost(navController = navController, startDestination = startDestination) {
                composable<LoginRoute> {
                    LoginScreen(
                        onLoginSuccess = { response -> 
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
                            navController.navigate(DashboardRoute) {
                                popUpTo(LoginRoute) { inclusive = true }
                            }
                        }
                    )
                }

                composable<DashboardRoute> {
                    DashboardScreen(
                        onNavigateToMyMatches = { navController.navigate(MyMatchesRoute) },
                        onNavigateToWhoViewedMe = { navController.navigate(WhoViewedMeRoute) },
                        onNavigateToProfileViewedByMe = { navController.navigate(ProfileViewedByMeRoute) },
                        onNavigateToMutualMatches = { navController.navigate(MutualMatchesRoute) },
                        onNavigateToNewlyJoined = { navController.navigate(NewlyJoinedRoute) },
                        onNavigateToContactViews = { navController.navigate(ContactViewsRoute) },
                        onNavigateToContactsViewedByMe = { navController.navigate(ContactsViewedByMeRoute) },
                        onLogout = { handleLogout() }
                    )
                }

                composable<MyMatchesRoute> {
                    MyMatchesScreen(
                        token = loggedInUser?.token ?: "",
                        candidateId = loggedInUser?.candidates?.firstOrNull()?.id ?: "",
                        onBack = { navController.popBackStack() },
                        onLogout = { handleLogout() }
                    )
                }

                composable<WhoViewedMeRoute> {
                    WhoViewedMeScreen(
                        token = loggedInUser?.token ?: "",
                        onBack = { navController.popBackStack() },
                        onLogout = { handleLogout() }
                    )
                }

                composable<ProfileViewedByMeRoute> {
                    ProfileViewedByMeScreen(
                        clientId = loggedInUser?.candidates?.firstOrNull()?.id ?: "",
                        token = loggedInUser?.token ?: "",
                        onBack = { navController.popBackStack() },
                        onLogout = { handleLogout() }
                    )
                }

                composable<MutualMatchesRoute> {
                    MutualMatchesScreen(
                        token = loggedInUser?.token ?: "",
                        candidateId = loggedInUser?.candidates?.firstOrNull()?.id ?: "",
                        onBack = { navController.popBackStack() },
                        onLogout = { handleLogout() }
                    )
                }

                composable<NewlyJoinedRoute> {
                    NewlyJoinedScreen(
                        token = loggedInUser?.token ?: "",
                        candidateId = loggedInUser?.candidates?.firstOrNull()?.id ?: "",
                        onBack = { navController.popBackStack() },
                        onLogout = { handleLogout() }
                    )
                }

                composable<ContactViewsRoute> {
                    ContactViewsScreen(
                        token = loggedInUser?.token ?: "",
                        onBack = { navController.popBackStack() },
                        onLogout = { handleLogout() }
                    )
                }

                composable<ContactsViewedByMeRoute> {
                    ContactsViewedByMeScreen(
                        token = loggedInUser?.token ?: "",
                        onBack = { navController.popBackStack() },
                        onLogout = { handleLogout() }
                    )
                }
            }
        }
    }
}

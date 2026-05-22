package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.features.auth.LoginScreen
import com.example.features.auth.OnboardingScreen
import com.example.features.auth.SignupScreen
import com.example.features.auth.SplashScreen
import com.example.features.carejourney.CareJourneyScreen
import com.example.features.cycle.CycleTrackerScreen
import com.example.features.lactation.LactationTrackerScreen
import com.example.features.community.CommunityScreen
import com.example.features.connect.ConnectScreen
import com.example.features.dashboard.DashboardScreen
import com.example.features.learning.LearningScreen
import com.example.features.profile.ProfileScreen
import com.example.features.recipes.RecipesScreen
import com.example.features.wellness.WellnessScreen
import com.example.ui.components.FloatingBottomNav
import com.example.ui.components.NavigationItem
import com.example.ui.theme.AftermaTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AftermaTheme {
                val mainViewModel: MainViewModel = viewModel()
                MainContentNavigator(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun MainContentNavigator(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "splash"

    val userLoggedIn by viewModel.userLoggedIn.collectAsState()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()

    // Monitor login logs state to route dynamic entry transitions automatically
    LaunchedEffect(userLoggedIn, onboardingCompleted) {
        if (currentRoute != "splash") {
            if (!onboardingCompleted) {
                navController.navigate("onboarding") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            } else if (!userLoggedIn) {
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            } else if (currentRoute == "login" || currentRoute == "signup" || currentRoute == "onboarding") {
                // If logged in, send directly to main landing dashboard
                navController.navigate("dashboard") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    }

    // 5 primary tabs for bottom navigation layout
    val navItems = listOf(
        NavigationItem("dashboard", "Sanctuary", Icons.Filled.Spa, Icons.Outlined.Spa),
        NavigationItem("care_journey", "Timeline", Icons.Filled.EventNote, Icons.Outlined.EventNote),
        NavigationItem("wellness", "Quiet Room", Icons.Filled.SelfImprovement, Icons.Outlined.SelfImprovement),
        NavigationItem("recipes", "Kitchen", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu),
        NavigationItem("community", "Embrace", Icons.Filled.Forum, Icons.Outlined.Forum)
    )

    val showBottomNav = currentRoute in navItems.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                FloatingBottomNav(
                    selectedRoute = currentRoute,
                    onRouteSelected = { route ->
                        navController.navigate(route) {
                            popUpTo("dashboard") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    items = navItems
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = if (showBottomNav) 0.dp else innerPadding.calculateBottomPadding()
                )
        ) {
            // Phase 3: Splash route
            composable("splash") {
                SplashScreen(
                    onSplashCompleted = {
                        if (!onboardingCompleted) {
                            navController.navigate("onboarding") {
                                popUpTo("splash") { inclusive = true }
                            }
                        } else if (!userLoggedIn) {
                            navController.navigate("login") {
                                popUpTo("splash") { inclusive = true }
                            }
                        } else {
                            navController.navigate("dashboard") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    }
                )
            }

            // Phase 3: Onboarding route
            composable("onboarding") {
                OnboardingScreen(
                    onOnboardingCompleted = {
                        viewModel.completeOnboarding()
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            // Phase 3: Login route
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { email, name ->
                        viewModel.loginUser(email, name)
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToSignup = {
                        navController.navigate("signup")
                    }
                )
            }

            // Phase 3: Signup route
            composable("signup") {
                SignupScreen(
                    onSignupSuccess = { email, name ->
                        viewModel.registerUser(email, name)
                        navController.navigate("dashboard") {
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                )
            }

            // Phase 4: Main Dashboard landing page
            composable("dashboard") {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToSection = { targetRoute ->
                        navController.navigate(targetRoute)
                    }
                )
            }

            // Phase 5: Care Journey Timeline
            composable("care_journey") {
                CareJourneyScreen(
                    viewModel = viewModel,
                    onNavigateToSection = { target ->
                        navController.navigate(target) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Phase 7: Period Log & Menstrual Cycle Tracking Screen
            composable("cycle_tracker") {
                CycleTrackerScreen(
                    viewModel = viewModel,
                    onNavigateToSection = { target ->
                        navController.navigate(target) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Phase 11: Lactation Log Tracking Screen
            composable("lactation_tracker") {
                LactationTrackerScreen(
                    viewModel = viewModel,
                    onNavigateToSection = { target ->
                        navController.navigate(target) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Phase 6: Mental Wellness quiet room
            composable("wellness") {
                WellnessScreen(viewModel = viewModel)
            }

            // Phase 7: Safe Recipes restorative kitchen
            composable("recipes") {
                RecipesScreen(viewModel = viewModel)
            }

            // Phase 8: Learning clinic
            composable("learning") {
                LearningScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                )
            }

            // Phase 9: Care Connect doctors
            composable("connect") {
                ConnectScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                )
            }

            // Phase 10: Community Forum
            composable("community") {
                CommunityScreen(viewModel = viewModel)
            }

            // Phase 11: Settings Profile
            composable("profile") {
                ProfileScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                )
            }
        }
    }
}

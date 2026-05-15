package com.example.karunada_kala.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.model.UserRole
import com.example.karunada_kala.ui.screens.*

@Composable
fun KalaNavGraph(navController: NavHostController, viewModel: KalaViewModel) {
    val forceLogout by viewModel.forceLogout.collectAsState()

    // -----------------------------------------------------------------------
    // Global observer: only redirect when a FORCED logout fires (user deleted
    // from Firebase). Manual logouts are handled inline per screen.
    // This avoids the "profile == null on startup → redirect to Login" race.
    // -----------------------------------------------------------------------
    LaunchedEffect(forceLogout) {
        if (forceLogout) {
            viewModel.onForceLogoutHandled()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToLogin = { role ->
                if (role == null) {
                    navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                } else if (role == UserRole.ADMIN) {
                    navController.navigate(Screen.AdminDashboard.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                } else {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                }
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess     = { role ->
                    if (role == UserRole.ADMIN) {
                        navController.navigate(Screen.AdminDashboard.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    } else {
                        navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    }
                },
                onNavigateRegister = { navController.navigate(Screen.Register.route) },
                viewModel = viewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onNavigateLogin   = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                    }
                },
                onAddArtForm   = { navController.navigate(Screen.AddArtForm.route) },
                onEditArtForm  = { id -> navController.navigate(Screen.EditArtForm.createRoute(id)) },
                onViewArtForm  = { id -> navController.navigate(Screen.ArtFormDetail.createRoute(id)) },
                onAddArtisan   = { navController.navigate(Screen.AddArtisan.route) },
                onEditArtisan  = { id -> navController.navigate(Screen.EditArtisan.createRoute(id)) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                viewModel = viewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navController  = navController,
                viewModel      = viewModel,
                onArtFormClick = { id -> navController.navigate(Screen.ArtFormDetail.createRoute(id)) },
                onEventClick   = { navController.navigate(Screen.EventFeed.route) },
                onMapClick     = { navController.navigate(Screen.ArtisanMap.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onLogout       = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ArtFormCatalog.route) {
            ArtFormCatalogScreen(
                navController  = navController,
                viewModel      = viewModel,
                onArtFormClick = { id -> navController.navigate(Screen.ArtFormDetail.createRoute(id)) },
                onAddArtForm   = { navController.navigate(Screen.AddArtForm.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AddArtForm.route) {
            AddArtFormScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess   = { navController.popBackStack() },
                viewModel   = viewModel
            )
        }

        composable(
            Screen.EditArtForm.route,
            arguments = listOf(navArgument("artFormId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artFormId = backStackEntry.arguments?.getString("artFormId") ?: ""
            EditArtFormScreen(
                artFormId   = artFormId,
                onBackClick = { navController.popBackStack() },
                onSuccess   = { navController.popBackStack() },
                viewModel   = viewModel
            )
        }

        composable(Screen.AddArtisan.route) {
            AddArtisanScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onSuccess   = { navController.popBackStack() },
                viewModel   = viewModel
            )
        }

        composable(
            Screen.EditArtisan.route,
            arguments = listOf(navArgument("artisanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId")
            AddArtisanScreen(
                navController = navController,
                artisanId   = artisanId,
                onBackClick = { navController.popBackStack() },
                onSuccess   = { navController.popBackStack() },
                viewModel   = viewModel
            )
        }

        composable(Screen.ArtFormDetail.route,
            arguments = listOf(navArgument("artFormId") { type = NavType.StringType })
        ) { back ->
            val artFormId = back.arguments?.getString("artFormId")
            val artForms by viewModel.artForms.collectAsState()
            val af = artForms.find { it.id == artFormId }
            if (af != null) ArtFormDetailScreen(artForm = af, onBackClick = { navController.popBackStack() })
        }

        composable(Screen.ArtisanMap.route) {
            ArtisanMapScreen(
                navController  = navController,
                onArtisanClick = { id -> navController.navigate(Screen.ArtisanProfile.createRoute(id)) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        composable(Screen.ArtisanProfile.route,
            arguments = listOf(navArgument("artisanId") { type = NavType.StringType })
        ) { back ->
            val artisanId = back.arguments?.getString("artisanId")
            val artisans by viewModel.artisans.collectAsState()
            val artisan = artisans.find { it.id == artisanId }
            if (artisan != null) ArtisanProfileScreen(
                artisan          = artisan,
                onBackClick      = { navController.popBackStack() },
                onWorkshopSignUp = { navController.navigate(Screen.WorkshopSignUp.createRoute(artisan.id)) }
            )
        }

        // ── FIXED: WorkshopSignUp now passes artisanId and viewModel ────────
        composable(
            Screen.WorkshopSignUp.route,
            arguments = listOf(navArgument("artisanId") { type = NavType.StringType })
        ) { back ->
            val artisanId = back.arguments?.getString("artisanId") ?: ""
            val artisans by viewModel.artisans.collectAsState()
            val artisan = artisans.find { it.id == artisanId }
            WorkshopSignUpScreen(
                artisanId       = artisanId,
                artisanName     = artisan?.name ?: "",
                artForms        = artisan?.artForms ?: emptyList(),
                onBackClick     = { navController.popBackStack() },
                onSubmitSuccess = {
                    navController.navigate(Screen.Confirmation.route) {
                        popUpTo(Screen.WorkshopSignUp.route) { inclusive = true }
                    }
                },
                viewModel       = viewModel
            )
        }

        composable(Screen.Confirmation.route) {
            ConfirmationScreen(onBackToMap = {
                navController.navigate(Screen.ArtisanMap.route) { popUpTo(Screen.Home.route) }
            })
        }

        composable(Screen.EventFeed.route) {
            EventFeedScreen(
                navController  = navController,
                viewModel      = viewModel,
                onPostEvent    = { navController.navigate(Screen.PostEvent.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onAboutClick = { navController.navigate(Screen.About.route) },
                onEditProfileClick = { navController.navigate(Screen.EditProfile.route) },
                onRegistrationsClick = { navController.navigate(Screen.Registrations.route) },
                onMyActivityClick = { navController.navigate(Screen.MyActivity.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                onPrivacyClick = { navController.navigate(Screen.PrivacySecurity.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        composable(Screen.About.route) {
            AboutScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.PostEvent.route) {
            PostEventScreen(
                onBackClick = { navController.popBackStack() },
                onSubmit    = { navController.popBackStack() },
                viewModel   = viewModel
            )
        }

        composable(Screen.Registrations.route) {
            ViewRegistrationsScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(
            Screen.LocationPicker.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType; defaultValue = "0.0" },
                navArgument("lng") { type = NavType.StringType; defaultValue = "0.0" }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lng = backStackEntry.arguments?.getString("lng")?.toDoubleOrNull()
            LocationPickerScreen(
                initialLatitude = lat,
                initialLongitude = lng,
                onLocationPicked = { pickedLat, pickedLng ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("picked_lat", pickedLat)
                    navController.previousBackStackEntry?.savedStateHandle?.set("picked_lng", pickedLng)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.MyActivity.route) {
            MyActivityScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(Screen.Notifications.route) {
            NotificationSettingsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.PrivacySecurity.route) {
            PrivacySecurityScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

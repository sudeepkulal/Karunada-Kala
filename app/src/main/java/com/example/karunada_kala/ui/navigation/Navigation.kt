package com.example.karunada_kala.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Splash         : Screen("splash")
    object Login          : Screen("login")
    object Register       : Screen("register")
    object Home           : Screen("home")
    object ArtFormCatalog : Screen("art_catalog")
    object ArtFormDetail  : Screen("art_detail/{artFormId}") {
        fun createRoute(id: String) = "art_detail/$id"
    }
    object ArtisanMap     : Screen("artisan_map")
    object ArtisanProfile : Screen("artisan_profile/{artisanId}") {
        fun createRoute(id: String) = "artisan_profile/$id"
    }
    object WorkshopSignUp : Screen("workshop_signup/{artisanId}") {
        fun createRoute(id: String) = "workshop_signup/$id"
    }
    object Confirmation   : Screen("confirmation")
    object EventFeed      : Screen("event_feed")
    object PostEvent      : Screen("post_event")
    object Profile        : Screen("profile")
    object AddArtForm     : Screen("add_art_form")
    object EditArtForm    : Screen("edit_art_form/{artFormId}") {
        fun createRoute(id: String) = "edit_art_form/$id"
    }
    object AddArtisan     : Screen("add_artisan")
    object EditArtisan    : Screen("edit_art_artisan/{artisanId}") {
        fun createRoute(id: String) = "edit_art_artisan/$id"
    }
    object About          : Screen("about")
    object AdminDashboard : Screen("admin_dashboard")
    object Registrations   : Screen("registrations")
    object EditProfile     : Screen("edit_profile")
    object MyActivity      : Screen("my_activity")
    object PrivacySecurity : Screen("privacy_security")
    object Notifications   : Screen("notifications")
    object LocationPicker  : Screen("location_picker?lat={lat}&lng={lng}") {
        fun createRoute(lat: Double?, lng: Double?) = "location_picker?lat=${lat ?: 0.0}&lng=${lng ?: 0.0}"
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

val bottomNavItems = listOf(
    BottomNavItem("Home",    Icons.Filled.Home,          Screen.Home.route),
    BottomNavItem("Explore", Icons.Filled.Palette,       Screen.ArtFormCatalog.route),
    BottomNavItem("Map",     Icons.Filled.Map,           Screen.ArtisanMap.route),
    BottomNavItem("Events",  Icons.Filled.CalendarMonth, Screen.EventFeed.route),
)
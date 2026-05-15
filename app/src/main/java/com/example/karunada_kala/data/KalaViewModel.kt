package com.example.karunada_kala.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunada_kala.model.ArtForm
import com.example.karunada_kala.model.Artisan
import com.example.karunada_kala.model.KalaEvent
import com.example.karunada_kala.model.UserProfile
import com.example.karunada_kala.model.WorkshopRegistration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class KalaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = KalaRepository()
    private val prefManager = PreferenceManager(application)
    private val auth = FirebaseAuth.getInstance()

    // ── User State ─────────────────────────────────────────────────────────────

    private val _currentUserProfile = MutableStateFlow<UserProfile?>(null)
    val currentUserProfile: StateFlow<UserProfile?> = _currentUserProfile

    /** true once the initial profile check is complete — prevents premature logout redirect */
    private val _profileLoaded = MutableStateFlow(false)
    val profileLoaded: StateFlow<Boolean> = _profileLoaded

    /** Signals NavGraph to navigate to Login because user was deleted from Firebase */
    private val _forceLogout = MutableStateFlow(false)
    val forceLogout: StateFlow<Boolean> = _forceLogout

    // ── Data State ─────────────────────────────────────────────────────────────

    private val _users = MutableStateFlow<List<UserProfile>>(emptyList())
    val users: StateFlow<List<UserProfile>> = _users

    private val _artForms = MutableStateFlow<List<ArtForm>>(emptyList())
    val artForms: StateFlow<List<ArtForm>> = _artForms

    private val _artisans = MutableStateFlow<List<Artisan>>(emptyList())
    val artisans: StateFlow<List<Artisan>> = _artisans

    private val _events = MutableStateFlow<List<KalaEvent>>(emptyList())
    val events: StateFlow<List<KalaEvent>> = _events

    private val _registrations = MutableStateFlow<List<WorkshopRegistration>>(emptyList())
    val registrations: StateFlow<List<WorkshopRegistration>> = _registrations

    private val _myRegistrations = MutableStateFlow<List<WorkshopRegistration>>(emptyList())
    val myRegistrations: StateFlow<List<WorkshopRegistration>> = _myRegistrations

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    // ── GenAI State ────────────────────────────────────────────────────────────

    /** Tracks which art form IDs are currently having descriptions generated */
    private val _generatingAiFor = MutableStateFlow<Set<String>>(emptySet())
    val generatingAiFor: StateFlow<Set<String>> = _generatingAiFor

    // ── Workshop Registration State ────────────────────────────────────────────

    /** Emits true when a workshop registration is submitted successfully */
    private val _registrationSuccess = MutableStateFlow(false)

    private val _registrationError = MutableStateFlow<String?>(null)
    
    private val _uiError = MutableStateFlow<String?>(null)
    val uiError: StateFlow<String?> = _uiError

    // ── Init ───────────────────────────────────────────────────────────────────

    init {
        loadInitialData()
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            prefManager.isDarkMode.collect {
                _isDarkMode.value = it
            }
        }
    }

    /**
     * Called once on ViewModel creation.
     * Loads all public data and fetches the current user's profile from Firestore.
     * After loading art forms, triggers Gemini AI for any that have no description.
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            val loadedArtForms = repository.getArtForms()
            _artForms.value = loadedArtForms
            _artisans.value = repository.getArtisans()
            _events.value = repository.getEvents()

            val user = auth.currentUser
            if (user != null) {
                val profile = repository.getUserProfile(user.uid)
                _currentUserProfile.value = profile
            }
            _profileLoaded.value = true

            // PRD §5.6: Auto-generate descriptions for art forms that have none stored
            generateMissingDescriptions(loadedArtForms)
        }
    }

    // ── Gemini AI Integration ──────────────────────────────────────────────────

    /**
     * PRD §5.6 GenAI Integration:
     * "The Gemini API auto-generates art form descriptions (100–200 words)
     *  when the Firestore field is empty."
     *
     * For each art form whose description is blank, calls the Gemini API.
     * On success, updates Firestore and the local StateFlow.
     * Falls back silently to the existing summary if Gemini fails or quota is exceeded.
     */
    private fun generateMissingDescriptions(artForms: List<ArtForm>) {
        artForms.filter { it.description.isBlank() }.forEach { artForm ->
            viewModelScope.launch {
                _generatingAiFor.value = _generatingAiFor.value + artForm.id

                val generated = GeminiRepository.generateArtFormDescription(
                    artFormName = artForm.name,
                    region = artForm.region
                )

                if (!generated.isNullOrBlank()) {
                    val updated = artForm.copy(
                        description = generated,
                        isAiGenerated = true
                    )
                    // Persist back to Firestore so it won't be re-generated next time
                    try {
                        repository.addArtForm(updated)
                    } catch (_: Exception) {
                        // Non-fatal: the in-memory version is still updated below
                    }
                    // Update local state
                    _artForms.value = _artForms.value.map {
                        if (it.id == artForm.id) updated else it
                    }
                }
                // If Gemini fails, the existing summary is shown — silent fallback (PRD §5.6)

                _generatingAiFor.value = _generatingAiFor.value - artForm.id
            }
        }
    }

    // ── Pull-to-Refresh ────────────────────────────────────────────────────────

    /**
     * Called on swipe-to-refresh.
     * Re-fetches all Firestore collections, re-validates the logged-in user,
     * and re-fetches the Firestore user document (catches role / name changes).
     */
    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true

            val freshArtForms = repository.getArtForms()
            _artForms.value = freshArtForms
            _artisans.value = repository.getArtisans()
            _events.value = repository.getEvents()
            _users.value = repository.getAllUsers()

            // Trigger AI generation for any newly added art forms without descriptions
            generateMissingDescriptions(freshArtForms)

            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                try {
                    firebaseUser.reload().await()
                    val profile = repository.getUserProfile(firebaseUser.uid)
                    if (profile == null) {
                        performForceLogout()
                    } else {
                        _currentUserProfile.value = profile
                    }
                } catch (e: FirebaseAuthInvalidUserException) {
                    performForceLogout()
                } catch (e: Exception) {
                    // Network error — keep existing state
                }
            }

            _isRefreshing.value = false
        }
    }

    private fun performForceLogout() {
        auth.signOut()
        _currentUserProfile.value = null
        _forceLogout.value = true
    }

    fun onForceLogoutHandled() {
        _forceLogout.value = false
    }

    // ── User Actions ───────────────────────────────────────────────────────────

    fun fetchAllUsers() {
        viewModelScope.launch {
            _users.value = repository.getAllUsers()
        }
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {
            try {
                repository.deleteUser(uid)
                fetchAllUsers()
            } catch (e: Exception) {
                _uiError.value = "Failed to delete user: ${e.localizedMessage}"
            }
        }
    }

    fun updateUserRole(uid: String, newRole: com.example.karunada_kala.model.UserRole) {
        viewModelScope.launch {
            repository.updateUserRole(uid, newRole)
            fetchAllUsers()
        }
    }

    fun deleteArtForm(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteArtForm(id)
                refreshData()
            } catch (e: Exception) {
                _uiError.value = "Failed to delete art form: ${e.localizedMessage}"
            }
        }
    }

    fun deleteArtisan(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteArtisan(id)
                refreshData()
            } catch (e: Exception) {
                _uiError.value = "Failed to delete artisan: ${e.localizedMessage}"
            }
        }
    }

    fun deleteEvent(id: String) {
        viewModelScope.launch {
            repository.deleteEvent(id)
            refreshData()
        }
    }

    fun fetchRegistrations(artisanId: String? = null) {
        viewModelScope.launch {
            _registrations.value = repository.getRegistrations(artisanId)
        }
    }

    fun fetchMyRegistrations() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            _myRegistrations.value = repository.getMyRegistrations(uid)
        }
    }

    fun updateProfile(name: String, phone: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val current = _currentUserProfile.value ?: return@launch
            val updated = current.copy(name = name, phone = phone)
            try {
                repository.updateUserProfile(updated)
                _currentUserProfile.value = updated
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Update failed")
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        _currentUserProfile.value = null
        onSuccess()
    }

    fun deleteOwnAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val user = auth.currentUser ?: return@launch
            try {
                val uid = user.uid
                // 1. Delete from Firestore
                repository.deleteUser(uid)
                // 2. Delete from Authentication
                user.delete().await()

                _currentUserProfile.value = null
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Please re-login to delete your account.")
            }
        }
    }

    // ── Content Actions ────────────────────────────────────────────────────────

    fun addEvent(event: KalaEvent, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.addEvent(event)
            _events.value = repository.getEvents()
            onSuccess()
        }
    }

    fun addArtForm(artForm: ArtForm, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.addArtForm(artForm)
            _artForms.value = repository.getArtForms()
            onSuccess()
        }
    }

    fun addArtisan(artisan: Artisan, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.addArtisan(artisan)
            _artisans.value = repository.getArtisans()
            onSuccess()
        }
    }

    // ── Workshop Registration ──────────────────────────────────────────────────

    /**
     * PRD §5.4 Workshop Sign-Up:
     * "Registration data is saved to Firestore 'registrations' collection within 2 seconds."
     */
    fun submitWorkshopRegistration(
        registration: WorkshopRegistration,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.addRegistration(registration)
                _registrationSuccess.value = true
                onSuccess()
            } catch (e: Exception) {
                val msg = e.message ?: "Registration failed. Please try again."
                _registrationError.value = msg
                onError(msg)
            }
        }
    }

    fun clearRegistrationState() {
        _registrationSuccess.value = false
        _registrationError.value = null
    }

    fun clearUiError() {
        _uiError.value = null
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            prefManager.setDarkMode(enabled)
        }
    }
}
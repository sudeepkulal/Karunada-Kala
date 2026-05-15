package com.example.karunada_kala.data

import com.example.karunada_kala.model.ArtForm
import com.example.karunada_kala.model.Artisan
import com.example.karunada_kala.model.KalaEvent
import com.example.karunada_kala.model.UserProfile
import com.example.karunada_kala.model.WorkshopRegistration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class KalaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ── Auth ──────────────────────────────────────────────────────────────────

    suspend fun getUserProfile(uid: String): UserProfile? {
        return db.collection("users").document(uid).get().await()
            .toObject(UserProfile::class.java)
    }

    suspend fun createUserProfile(profile: UserProfile) {
        db.collection("users").document(profile.uid).set(profile).await()
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        db.collection("users").document(profile.uid).set(profile).await()
    }

    fun getCurrentUser() = auth.currentUser

    // ── Admin: Manage Users ───────────────────────────────────────────────────

    suspend fun getAllUsers(): List<UserProfile> {
        return db.collection("users").get().await().toObjects(UserProfile::class.java)
    }

    suspend fun deleteUser(uid: String) {
        db.collection("users").document(uid).delete().await()
    }

    suspend fun updateUserRole(uid: String, newRole: com.example.karunada_kala.model.UserRole) {
        db.collection("users").document(uid).update("role", newRole.name).await()
    }

    suspend fun deleteAuthAccount() {
        auth.currentUser?.delete()?.await()
    }

    // ── Events ────────────────────────────────────────────────────────────────

    suspend fun addEvent(event: KalaEvent) {
        val docRef = if (event.id.isEmpty())
            db.collection("events").document()
        else
            db.collection("events").document(event.id)
        val finalEvent = event.copy(id = docRef.id)
        docRef.set(finalEvent).await()
    }

    suspend fun getEvents(): List<KalaEvent> {
        return try {
            db.collection("events").get().await().toObjects(KalaEvent::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deleteEvent(id: String) {
        db.collection("events").document(id).delete().await()
    }

    // ── Art Forms ─────────────────────────────────────────────────────────────

    suspend fun addArtForm(artForm: ArtForm) {
        val docRef = if (artForm.id.isEmpty())
            db.collection("artForms").document()
        else
            db.collection("artForms").document(artForm.id)
        val finalArtForm = artForm.copy(id = docRef.id)
        docRef.set(finalArtForm).await()
    }

    suspend fun getArtForms(): List<ArtForm> {
        return try {
            db.collection("artForms").get().await().toObjects(ArtForm::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deleteArtForm(id: String) {
        db.collection("artForms").document(id).delete().await()
    }

    // ── Artisans ──────────────────────────────────────────────────────────────

    suspend fun addArtisan(artisan: Artisan) {
        val docRef = if (artisan.id.isEmpty())
            db.collection("artisans").document()
        else
            db.collection("artisans").document(artisan.id)
        val finalArtisan = artisan.copy(id = docRef.id)
        docRef.set(finalArtisan).await()
    }

    suspend fun getArtisans(): List<Artisan> {
        return try {
            db.collection("artisans").get().await().toObjects(Artisan::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deleteArtisan(id: String) {
        db.collection("artisans").document(id).delete().await()
    }

    // ── Workshop Registrations ────────────────────────────────────────────────

    /**
     * Saves a workshop sign-up to Firestore's 'registrations' collection.
     * PRD §5.4: "Registration data is saved to Firestore 'registrations'
     * collection within 2 seconds."
     */
    suspend fun addRegistration(registration: WorkshopRegistration) {
        val docRef = db.collection("registrations").document()
        val finalReg = registration.copy(id = docRef.id)
        docRef.set(finalReg).await()
    }

    suspend fun getRegistrations(artisanId: String? = null): List<WorkshopRegistration> {
        return try {
            val query = if (artisanId != null) {
                db.collection("registrations").whereEqualTo("artisanId", artisanId)
            } else {
                db.collection("registrations")
            }
            query.get().await().toObjects(WorkshopRegistration::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMyRegistrations(userId: String): List<WorkshopRegistration> {
        return try {
            db.collection("registrations").whereEqualTo("userId", userId)
                .get().await().toObjects(WorkshopRegistration::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
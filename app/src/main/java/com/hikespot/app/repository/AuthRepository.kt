package com.hikespot.app.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // Sign Up User
    suspend fun signUp(username: String, email: String, password: String): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
        user?.let {
            val userData = hashMapOf(
                "id" to it.uid,
                "username" to username,
                "email" to email,
                "profileImage" to ""
            )
            firestore.collection("users").document(it.uid).set(userData).await()
        }
        return user
    }

    // Login User
    suspend fun login(email: String, password: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    // Update Username
    suspend fun updateUsername(uid: String, newUsername: String) {
        firestore.collection("users").document(uid).update("username", newUsername).await()
    }

    // Upload Profile Image & Update Firestore
    suspend fun updateProfileImage(uid: String, imageUri: String): String {
        val storageRef = storage.reference.child("profile_images/$uid.jpg")
        val uploadTask = storageRef.putFile(android.net.Uri.parse(imageUri)).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()
        firestore.collection("users").document(uid).update("profileImage", downloadUrl).await()
        return downloadUrl
    }

    // Get Current User
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

}
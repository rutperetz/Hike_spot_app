package com.hikespot.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.hikespot.app.model.User
import com.hikespot.app.repository.AuthRepository
import com.hikespot.app.utils.UserManager
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authResult = MutableLiveData<FirebaseUser?>()
    val authResult: LiveData<FirebaseUser?> get() = _authResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _userLiveData = MutableLiveData<User?>()
    val userLiveData: LiveData<User?> get() = _userLiveData

    fun loadUser() {
        viewModelScope.launch {
            val firebaseUser = authRepository.getCurrentUser()
            firebaseUser?.let {
                val user = authRepository.getUserDetails(it.uid)
                user?.let { userData ->
                    UserManager.setUser(userData) // Store globally
                    _userLiveData.value = userData
                }
            }
        }
    }

    // Sign Up
    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = authRepository.signUp(username, email, password)
                _authResult.value = user
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = authRepository.login(email, password)
                _authResult.value = user
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Update Username
    fun updateUsername(uid: String, newUsername: String) {
        viewModelScope.launch {
            try {
                authRepository.updateUsername(uid, newUsername)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Update Profile Image
    fun updateProfileImage(uid: String, imageUri: String) {
        viewModelScope.launch {
            try {
                authRepository.updateProfileImage(uid, imageUri)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Get Current User
    fun getCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }
}
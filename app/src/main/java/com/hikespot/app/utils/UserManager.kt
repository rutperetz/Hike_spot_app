package com.hikespot.app.utils

import com.hikespot.app.model.User


object UserManager {
    var currentUser: User? = null

    fun setUser(user: User) {
        currentUser = user
    }

    fun getUser(): User? {
        return currentUser
    }

    fun clearUser() {
        currentUser = null
    }
}
package com.hikespot.app.interfaces

interface PostCallback {
    fun onSuccess(message: String)
    fun onFailure(error: String)
}

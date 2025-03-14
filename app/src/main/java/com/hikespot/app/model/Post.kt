package com.hikespot.app.model

import java.io.Serializable

data class Post(
    val id: String,
    val description: String,
    val location: String,
    val photoUrl: String,
    val username: String,
    val userProfileImage:String,
    val userId:String,
    val timestamp: Long
) : Serializable{
    constructor() : this("","","","","","","",0)
}

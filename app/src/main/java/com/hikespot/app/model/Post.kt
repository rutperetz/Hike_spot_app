package com.hikespot.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: String,
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

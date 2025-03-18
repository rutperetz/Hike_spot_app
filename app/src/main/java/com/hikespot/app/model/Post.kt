package com.hikespot.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: String,
    var description: String,
    var location: String,
    var photoUrl: String,
    val username: String,
    val userProfileImage:String,
    val userId:String,
    val timestamp: Long,
    var likes: MutableList<String> = mutableListOf(),
    var dislikes: MutableList<String> = mutableListOf()
) : Serializable{
    constructor() : this("", "", "", "", "", "", "", 0, mutableListOf(), mutableListOf())

    fun getLikesString(): String = likes.joinToString(",") // Convert list to string
    fun getDislikesString(): String = dislikes.joinToString(",")

    fun setLikesFromString(likesStr: String) {
        likes = if (likesStr.isNotEmpty()) likesStr.split(",").toMutableList() else mutableListOf()
    }

    fun setDislikesFromString(dislikesStr: String) {
        dislikes = if (dislikesStr.isNotEmpty()) dislikesStr.split(",").toMutableList() else mutableListOf()
    }
}

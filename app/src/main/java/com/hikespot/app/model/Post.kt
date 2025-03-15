package com.hikespot.app.model

<<<<<<< HEAD
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: String,
=======
import java.io.Serializable

data class Post(
    val id: String,
>>>>>>> 5e2d23188b793fc41417b237edfa726052ee665f
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

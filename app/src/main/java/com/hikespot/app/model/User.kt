package com.hikespot.app.model

import java.io.Serializable

data class User(
    val id:String,
    val username:String,
    val email:String,
    val profileImage:String,
):Serializable{
    constructor():this("","","","")
}

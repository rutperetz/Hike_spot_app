package com.hikespot.app.room

import androidx.room.*
import com.hikespot.app.model.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Update
    suspend fun updatePost(post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    suspend fun getAllPosts(): List<Post>

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getPostsByUserId(userId: String): List<Post>

    @Query("SELECT * FROM posts WHERE location = :location ORDER BY timestamp DESC")
    suspend fun getPostsByLocation(location: String): List<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)

    @Query("UPDATE posts SET likes = :likesStr, dislikes = :dislikesStr WHERE id = :postId")
    suspend fun updateLikesDislikes(postId: String, likesStr: String, dislikesStr: String)
}

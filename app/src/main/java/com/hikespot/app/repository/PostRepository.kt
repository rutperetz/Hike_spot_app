package com.hikespot.app.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.hikespot.app.interfaces.PostCallback
import com.hikespot.app.model.Post
import com.hikespot.app.room.PostDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostRepository(private val postDao: PostDao) {
    private val firestore = FirebaseFirestore.getInstance()
    private val postCollection = firestore.collection("posts")
    private val storageRef = FirebaseStorage.getInstance().reference.child("post_images")

    fun getPostId():String{
        return postCollection.document().id
    }

    suspend fun syncPostsFromFirestore() {
        postCollection.get().addOnSuccessListener { snapshot ->
            val posts = snapshot.toObjects(Post::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                postDao.insertPosts(posts)
            }
        }
    }

    fun toggleLike(postId: String, userId: String, onComplete: (Post?) -> Unit) {
        val postRef = postCollection.document(postId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val post = snapshot.toObject(Post::class.java) ?: return@runTransaction null

            if (post.likes.contains(userId)) {
                post.likes.remove(userId)
            } else {
                post.likes.add(userId)
                post.dislikes.remove(userId)
            }

            transaction.set(postRef, post)
            return@runTransaction post
        }.addOnSuccessListener { updatedPost ->
            updatedPost?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    postDao.updateLikesDislikes(it.id, it.getLikesString(), it.getDislikesString()) // Update Room
                }
            }
            onComplete(updatedPost)
        }
    }

    fun toggleDislike(postId: String, userId: String, onComplete: (Post?) -> Unit) {
        val postRef = postCollection.document(postId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val post = snapshot.toObject(Post::class.java) ?: return@runTransaction null

            if (post.dislikes.contains(userId)) {
                post.dislikes.remove(userId)
            } else {
                post.dislikes.add(userId)
                post.likes.remove(userId)
            }

            transaction.set(postRef, post)
            return@runTransaction post
        }.addOnSuccessListener { updatedPost ->
            updatedPost?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    postDao.updateLikesDislikes(it.id, it.getLikesString(), it.getDislikesString()) // Update Room
                }
            }
            onComplete(updatedPost)
        }
    }

    suspend fun insertPost(post: Post, imageUri: Uri?, callback: PostCallback) {
        try {
            postDao.insertPost(post)

            if (imageUri != null) {
                val updatedPost = post.copy(photoUrl = "")
                postCollection.document(post.id).set(updatedPost).await()

                uploadImageAndUpdatePost(post, imageUri, callback)
            } else {
                postCollection.document(post.id).set(post).await()
                callback.onSuccess("Post added successfully!")
            }
        } catch (e: Exception) {
            callback.onFailure("Failed to insert post: ${e.message}")
        }
    }

    private suspend fun uploadImageAndUpdatePost(post: Post, imageUri: Uri, callback: PostCallback) {
        try {
            val imageRef = storageRef.child("posts/${post.id}.jpg")
            val uploadTask = imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()

            postDao.updatePost(Post(post.id, post.description, post.location, downloadUrl, post.username, post.userProfileImage, post.userId, post.timestamp))
            postCollection.document(post.id).update("photoUrl", downloadUrl).await()

            callback.onSuccess("Post added successfully!")
        } catch (e: Exception) {
            callback.onFailure("Image upload failed: ${e.message}")
        }
    }

    suspend fun updatePost(post: Post, imageUri: Uri?, callback: PostCallback) {
        try {
            if (imageUri != null) {

                post.photoUrl?.let { previousUrl ->
                    deletePreviousImage(previousUrl)
                }


                val newPhotoUrl = uploadNewImage(post.id, imageUri)


                val updatedPost = post.copy(photoUrl = newPhotoUrl)
                postDao.updatePost(updatedPost)
                postCollection.document(post.id).set(updatedPost).await()
            } else {

                postDao.updatePost(post)
                postCollection.document(post.id).update(
                    "description", post.description,
                    "location", post.location,
                    "photoUrl", post.photoUrl
                ).await()
            }
            callback.onSuccess("Post updated successfully!")
        } catch (e: Exception) {
            callback.onFailure("Failed to update post: ${e.message}")
        }
    }

    private suspend fun deletePreviousImage(photoUrl: String) {
        try {
            val imageRef = Firebase.storage.getReferenceFromUrl(photoUrl)
            imageRef.delete().await()
        } catch (e: Exception) {
            Log.e("FirebaseStorage", "Failed to delete previous image: ${e.message}")
        }
    }

    private suspend fun uploadNewImage(postId: String, imageUri: Uri): String {
        return try {
            val imageRef = storageRef.child("posts/$postId.jpg")
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw Exception("Image upload failed: ${e.message}")
        }
    }



    suspend fun deletePost(post: Post, callback: PostCallback) {
        try {
            postDao.deletePost(post)
            postCollection.document(post.id).delete().await()
            callback.onSuccess("Post deleted successfully!")
        } catch (e: Exception) {
            callback.onFailure("Failed to delete post: ${e.message}")
        }
    }

    suspend fun getAllPosts(callback: (List<Post>?, String?) -> Unit) {
        try {
            val posts = postDao.getAllPosts()
            callback(posts, null)
        } catch (e: Exception) {
            callback(null, "Failed to fetch posts: ${e.message}")
        }
    }

    suspend fun getPostsByUserId(userId: String, callback: (List<Post>?, String?) -> Unit) {
        try {
            val posts = postDao.getPostsByUserId(userId)
            callback(posts, null)
        } catch (e: Exception) {
            callback(null, "Failed to fetch posts by user: ${e.message}")
        }
    }

    suspend fun getPostsByLocation(location: String, callback: (List<Post>?, String?) -> Unit) {
        try {
            val posts = postDao.getPostsByLocation(location)
            callback(posts, null)
        } catch (e: Exception) {
            callback(null, "Failed to fetch posts by user: ${e.message}")
        }
    }
}



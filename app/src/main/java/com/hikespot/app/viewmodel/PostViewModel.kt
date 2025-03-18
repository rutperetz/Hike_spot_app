package com.hikespot.app.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.hikespot.app.interfaces.PostCallback
import com.hikespot.app.model.Post
import com.hikespot.app.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>?>()
    private val _userPosts = MutableLiveData<List<Post>?>()
    private val _searchPosts = MutableLiveData<List<Post>?>()
    val posts: LiveData<List<Post>?> get() = _posts
    val userPosts: LiveData<List<Post>?> get() = _userPosts
    val searchPosts: LiveData<List<Post>?> get() = _searchPosts

    private val _operationResult = MutableLiveData<String?>()
    val operationResult: LiveData<String?> get() = _operationResult

    fun getPostId():String{
        return repository.getPostId()
    }

    init {
        viewModelScope.launch {
            repository.syncPostsFromFirestore()
        }
    }

    fun toggleLike(postId: String, userId: String,onComplete: (Post?) -> Unit) {
        repository.toggleLike(postId, userId) { updatedPost ->
            updatedPost?.let {
                onComplete(updatedPost)
            //updatePostInList(it) }
            }
        }
    }

    fun toggleDislike(postId: String, userId: String,onComplete: (Post?) -> Unit) {
        repository.toggleDislike(postId, userId) { updatedPost ->
            updatedPost?.let {
                onComplete(updatedPost)
            //updatePostInList(it) }
            }
        }
    }

    private fun updatePostInList(updatedPost: Post) {
        // Since LiveData is being observed, the UI updates automatically.
        Log.d("TEST000",updatedPost.userId)
    }

    fun insertPost(post: Post, imageUri: Uri?, callback: PostCallback) {
        viewModelScope.launch {
            repository.insertPost(post, imageUri, callback)
        }
    }

    fun updatePost(post: Post,imageUri: Uri?, callback: PostCallback) {
        viewModelScope.launch {
            repository.updatePost(post,imageUri, callback)
        }
    }

    fun deletePost(post: Post, callback: PostCallback) {
        viewModelScope.launch {
            repository.deletePost(post, callback)
        }
    }

    fun fetchAllPosts() {
        viewModelScope.launch {
            repository.getAllPosts { posts, error ->
                if (error == null) {
                    _posts.postValue(posts)
                } else {
                    _operationResult.postValue(error)
                }
            }
        }
    }

    fun fetchPostsByUserId(userId: String) {
        viewModelScope.launch {
            repository.getPostsByUserId(userId) { posts, error ->
                if (error == null) {
                    _userPosts.postValue(posts)
                } else {
                    _operationResult.postValue(error)
                }
            }
        }
    }

    fun fetchPostsByLocation(location: String) {
        viewModelScope.launch {
            repository.getPostsByLocation(location) { posts, error ->
                if (error == null) {
                    _searchPosts.postValue(posts)
                } else {
                    _operationResult.postValue(error)
                }
            }
        }
    }
}



package com.hikespot.app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hikespot.app.R
import com.hikespot.app.adapters.PersonalPostAdapter
import com.hikespot.app.adapters.PostAdapter
import com.hikespot.app.databinding.FragmentFeedBinding
import com.hikespot.app.model.Post
import com.hikespot.app.repository.PostRepository
import com.hikespot.app.room.PostDatabase
import com.hikespot.app.utils.UserManager
import com.hikespot.app.viewmodel.PostViewModel
import com.hikespot.app.viewmodelfactory.PostViewModelFactory


class FeedFragment : Fragment() {

    private lateinit var binding:FragmentFeedBinding
    private val postsList = mutableListOf<Post>()
    private lateinit var database: PostDatabase
    private lateinit var repository: PostRepository
    private lateinit var viewModelFactory: PostViewModelFactory
    private lateinit var postViewModel: PostViewModel
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = PostDatabase.getDatabase(requireActivity())
        repository = PostRepository(database.postDao())
        viewModelFactory = PostViewModelFactory(repository)
        postViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[PostViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        getPosts()
    }

    private fun getPosts(){
        postViewModel.fetchAllPosts()
        postViewModel.posts.observe(requireActivity()){list->
            if (list!!.isNotEmpty()){
                postsList.clear()
                postsList.addAll(list)
                adapter.notifyItemChanged(0,postsList.size)
            }
        }

    }

    private fun setUpRecyclerView(){
        binding.feedPostRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        binding.feedPostRecyclerview.hasFixedSize()
        adapter = PostAdapter(postsList)
        binding.feedPostRecyclerview.adapter = adapter
        adapter.setOnItemClickListener(object : PostAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, post: Post) {

            }

        })
    }

}
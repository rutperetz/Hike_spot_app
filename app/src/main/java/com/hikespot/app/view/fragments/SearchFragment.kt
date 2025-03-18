package com.hikespot.app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hikespot.app.R
import com.hikespot.app.adapters.PostAdapter
import com.hikespot.app.databinding.FragmentSearchBinding
import com.hikespot.app.model.Post
import com.hikespot.app.model.User
import com.hikespot.app.repository.PostRepository
import com.hikespot.app.room.PostDatabase
import com.hikespot.app.utils.UserManager
import com.hikespot.app.viewmodel.PostViewModel
import com.hikespot.app.viewmodelfactory.PostViewModelFactory

class SearchFragment : Fragment() {

    private lateinit var binding:FragmentSearchBinding
    private val postsList = mutableListOf<Post>()
    private lateinit var database: PostDatabase
    private lateinit var repository: PostRepository
    private lateinit var viewModelFactory: PostViewModelFactory
    private lateinit var postViewModel: PostViewModel
    private lateinit var adapter: PostAdapter
    private var user: User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (user == null){
            user = UserManager.getUser()
        }
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
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        binding.filterGroup.setOnCheckedChangeListener { group, checkedId ->
           when (checkedId) {
                R.id.cb_search_center -> {
                    getPosts("Center")
                }
                R.id.cb_search_north -> {
                    getPosts("North")
                }
                R.id.cb_search_south -> {
                    getPosts("South")
                }
                R.id.cb_search_lowlands -> {
                    getPosts("Lowlands")
                }
                else -> {
                    getPosts(null)
                }
            }
        }
    }

    private fun getPosts(location:String?){
        if (location != null){
            binding.loginProgressBar.visibility = View.VISIBLE
            postViewModel.fetchPostsByLocation(location)
        }
        postViewModel.searchPosts.observe(requireActivity()){list->
            binding.loginProgressBar.visibility = View.GONE
            if (list!!.isNotEmpty()){
                postsList.clear()
                postsList.addAll(list)
                adapter.notifyItemChanged(0,postsList.size)
            }
            else{
                postsList.clear()
                adapter.notifyDataSetChanged()
               }
        }
    }

    private fun setUpRecyclerView(){
        binding.recyclerViewSearchPosts.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerViewSearchPosts.hasFixedSize()
        adapter = PostAdapter(postsList)
        binding.recyclerViewSearchPosts.adapter = adapter
        adapter.setOnItemClickListener(object : PostAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, post: Post) {

            }

            override fun onItemLikeClick(position: Int, post: Post) {
                postViewModel.toggleLike(post.id,user!!.id){updatedPost->
                    postsList.removeAt(position)
                    postsList.add(position,updatedPost!!)
                    adapter.notifyItemChanged(position)
                }
            }

            override fun onItemDisLikeClick(position: Int, post: Post) {
                postViewModel.toggleDislike(post.id,user!!.id){updatedPost->
                    postsList.removeAt(position)
                    postsList.add(position,updatedPost!!)
                    adapter.notifyItemChanged(position)
                }
            }

        })
    }
}
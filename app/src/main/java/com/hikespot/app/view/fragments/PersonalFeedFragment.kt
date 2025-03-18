package com.hikespot.app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hikespot.app.R
import com.hikespot.app.adapters.PersonalPostAdapter
import com.hikespot.app.databinding.FragmentPersonalFeedBinding
import com.hikespot.app.model.Post
import com.hikespot.app.model.User
import com.hikespot.app.repository.PostRepository
import com.hikespot.app.room.PostDatabase
import com.hikespot.app.utils.UserManager
import com.hikespot.app.viewmodel.PostViewModel
import com.hikespot.app.viewmodel.WeatherViewModel
import com.hikespot.app.viewmodelfactory.PostViewModelFactory
import com.squareup.picasso.Picasso


class PersonalFeedFragment : Fragment() {
    private lateinit var binding:FragmentPersonalFeedBinding
    private val personalPostList = mutableListOf<Post>()
    private lateinit var database: PostDatabase
    private lateinit var repository: PostRepository
    private lateinit var viewModelFactory: PostViewModelFactory
    private lateinit var postViewModel: PostViewModel
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var adapter:PersonalPostAdapter
    private var user:User?=null
    val apiKey = "e572674eea9c73a4e16cf8e04e675e9a" // Replace with your API key
    val city = "Tel Aviv"

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
        binding =  FragmentPersonalFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsProfileButton.setOnClickListener {
         findNavController().navigate(R.id.action_personal_feed_to_edit_profile)
        }

        setUpRecyclerView()
    }

    private fun getUserPosts(){
        postViewModel.fetchPostsByUserId(user!!.id)
        postViewModel.userPosts.observe(requireActivity()){list->
            if (list!!.isNotEmpty()){
                personalPostList.clear()
                personalPostList.addAll(list)
                adapter.notifyItemChanged(0,personalPostList.size)
            }
        }

    }

    private fun setUpRecyclerView(){
        binding.recyclerViewPersonalPosts.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerViewPersonalPosts.hasFixedSize()
        adapter = PersonalPostAdapter(personalPostList)
        binding.recyclerViewPersonalPosts.adapter = adapter
        adapter.setOnItemClickListener(object : PersonalPostAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, post: Post) {

            }

            override fun onItemSettingClick(position: Int, post: Post) {
                val bundle = Bundle().apply {
                    putSerializable("post", post)
                }
                  findNavController().navigate(R.id.action_personal_feed_to_edit_post,bundle)
            }

            override fun onItemLikeClick(position: Int, post: Post) {
                postViewModel.toggleLike(post.id,"${UserManager.getUser()?.id}"){updatedPost->
                    personalPostList.removeAt(position)
                    personalPostList.add(position,updatedPost!!)
                    adapter.notifyItemChanged(position)
                }
            }

            override fun onItemDisLikeClick(position: Int, post: Post) {
                postViewModel.toggleDislike(post.id,"${UserManager.getUser()?.id}"){updatedPost->
                    personalPostList.removeAt(position)
                    personalPostList.add(position,updatedPost!!)
                    adapter.notifyItemChanged(position)
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        displayUserInfo()
        getUserPosts()
        displayWeatherDetails()
    }

    private fun displayUserInfo(){

         val user = UserManager.getUser()

        user?.apply {
            if (user.profileImage.isNotEmpty()){
                Picasso.get().load(user.profileImage)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .resize(200,200)
                    .centerCrop()
                    .into(binding.profileImage)
            }
            binding.username.text = user.username
        }
    }

    private fun displayWeatherDetails(){
        weatherViewModel.fetchWeather(city, apiKey)
        weatherViewModel.weatherData.observe(this, Observer { weather ->
            weather?.let {
                binding.weatherTextView.setTextColor(ContextCompat.getColor(requireActivity(),R.color.green))
                binding.weatherTextView.text = "City: ${it.name}\nTemp: ${it.main.temp}°C\nHumidity: ${it.main.humidity}%\nDescription: ${it.weather[0].description}"
            } ?: run {
                binding.weatherTextView.setTextColor(ContextCompat.getColor(requireActivity(),R.color.red))
                binding.weatherTextView.text = "Failed to load weather data, Check API KEY"
            }
        })
    }

}
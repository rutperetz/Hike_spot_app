package com.hikespot.app.view.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hikespot.app.R
import com.hikespot.app.databinding.FragmentNewPostBinding
import com.hikespot.app.interfaces.PostCallback
import com.hikespot.app.model.Post
import com.hikespot.app.model.User
import com.hikespot.app.repository.PostRepository
import com.hikespot.app.room.PostDatabase
import com.hikespot.app.utils.UserManager
import com.hikespot.app.viewmodel.PostViewModel
import com.hikespot.app.viewmodelfactory.PostViewModelFactory
import com.squareup.picasso.Picasso


class NewPostFragment : Fragment() {

    private lateinit var binding:FragmentNewPostBinding
    private var user:User?=null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null
    private var selectedLocation: String = ""
    private lateinit var database:PostDatabase
    private lateinit var repository: PostRepository
    private lateinit var viewModelFactory: PostViewModelFactory
    private lateinit var postViewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (user == null){
            user = UserManager.getUser()
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.postImageView.setImageURI(uri)
                binding.postImageView.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            }
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
        binding =  FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPhotoButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.postButton.setOnClickListener {
            // Validate input
            if (binding.postEditText.text.isEmpty() && selectedImageUri == null) {
                Toast.makeText(requireContext(), "Please add text or image to your post", Toast.LENGTH_SHORT).show()
            }
            else if (selectedLocation.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT).show()
            }
            else{
                binding.loginProgressBar.visibility = View.VISIBLE
                binding.postEditText.isEnabled = false
                binding.locationGroup.isEnabled = false
                binding.postButton.isEnabled = false
                binding.addPhotoButton.isEnabled = false
                val newPost = Post(
                    id = postViewModel.getPostId(),
                    description = binding.postEditText.text.toString(),
                    location = selectedLocation,
                    photoUrl = "",
                    username = user!!.username,
                    userProfileImage = user!!.profileImage,
                    userId = user!!.id,
                    timestamp = System.currentTimeMillis()
                )

                postViewModel.insertPost(newPost, selectedImageUri, object : PostCallback {
                    override fun onSuccess(message: String) {
                        binding.loginProgressBar.visibility = View.GONE
                        binding.postEditText.isEnabled = true
                        binding.locationGroup.isEnabled = true
                        binding.postButton.isEnabled = true
                        binding.addPhotoButton.isEnabled = true
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                        resetForm()
                        findNavController().navigate(R.id.feed)
                    }

                    override fun onFailure(error: String) {
                        binding.loginProgressBar.visibility = View.GONE
                        binding.postEditText.isEnabled = true
                        binding.locationGroup.isEnabled = true
                        binding.postButton.isEnabled = true
                        binding.addPhotoButton.isEnabled = true
                        Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        binding.locationGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedLocation = when (checkedId) {
                R.id.cb_center -> "Center"
                R.id.cb_north -> "North"
                R.id.cb_south -> "South"
                R.id.cb_lowlands -> "Lowlands"
                else -> ""
            }
        }
    }

    private fun resetForm() {
        binding.postEditText.text.clear()
        binding.locationGroup.clearCheck()
        selectedLocation = ""
        selectedImageUri = null
    }

    override fun onResume() {
        super.onResume()
        displayUserInfo()
    }

    private fun displayUserInfo(){

        user?.apply {
            if (user!!.profileImage.isNotEmpty()){
                Picasso.get().load(user!!.profileImage)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .resize(200,200)
                    .centerCrop()
                    .into(binding.profileImage)
            }
            binding.usernameNewpost.text = user!!.username
        }
    }

}
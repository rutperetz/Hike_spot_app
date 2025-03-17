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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hikespot.app.R
import com.hikespot.app.databinding.FragmentEditPostBinding
import com.hikespot.app.interfaces.PostCallback
import com.hikespot.app.model.Post
import com.hikespot.app.model.User
import com.hikespot.app.repository.PostRepository
import com.hikespot.app.room.PostDatabase
import com.hikespot.app.utils.UserManager
import com.hikespot.app.viewmodel.PostViewModel
import com.hikespot.app.viewmodelfactory.PostViewModelFactory
import com.squareup.picasso.Picasso


class EditPostFragment : Fragment() {

    private lateinit var binding: FragmentEditPostBinding
    private var user: User? = null
    private var post: Post? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null
    private var selectedLocation: String = ""
    private lateinit var database: PostDatabase
    private lateinit var repository: PostRepository
    private lateinit var viewModelFactory: PostViewModelFactory
    private lateinit var postViewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (user == null) {
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
        postViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[PostViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        post = arguments?.getSerializable("post") as Post

        displayPostDetails()

        binding.addPhotoButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.postButton.setOnClickListener {
            // Validate input
            if (binding.postEditText.text.isEmpty() && selectedImageUri == null) {
                Toast.makeText(
                    requireContext(),
                    "Please add text or image to your post",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (selectedLocation.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.loginProgressBar.visibility = View.VISIBLE
                binding.postEditText.isEnabled = false
                binding.locationGroup.isEnabled = false
                binding.postButton.isEnabled = false
                binding.addPhotoButton.isEnabled = false

                post!!.description = binding.postEditText.text.toString()
                post!!.location = selectedLocation
                post!!.photoUrl = if (selectedImageUri == null) {
                    post!!.photoUrl
                } else {
                    ""
                }

                postViewModel.updatePost(post!!, selectedImageUri, object : PostCallback {
                    override fun onSuccess(message: String) {
                        binding.loginProgressBar.visibility = View.GONE
                        binding.postEditText.isEnabled = true
                        binding.locationGroup.isEnabled = true
                        binding.postButton.isEnabled = true
                        binding.addPhotoButton.isEnabled = true
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
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

        binding.deletePostButton.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireActivity())
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to delete this post?")

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Dismiss the dialog when "No" is clicked
            }

            builder.setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                binding.loginProgressBar.visibility = View.VISIBLE
                postViewModel.deletePost(post!!,object :PostCallback{
                    override fun onSuccess(message: String) {
                        binding.loginProgressBar.visibility = View.GONE
                        binding.postEditText.isEnabled = true
                        binding.locationGroup.isEnabled = true
                        binding.postButton.isEnabled = true
                        binding.addPhotoButton.isEnabled = true
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
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

            builder.show()

        }
    }

    private fun displayPostDetails() {
        post?.let {
            Picasso.get().load(post!!.userProfileImage)
                .placeholder(R.drawable.placeholder)
                .resize(200, 200)
                .centerCrop()
                .into(binding.profileImage)
            binding.usernameNewpost.text = post!!.username
            selectedLocation = post!!.location
            if (selectedLocation == "Center") {
                binding.cbCenter.isChecked = true
            } else if (selectedLocation == "North") {
                binding.cbNorth.isChecked = true
            } else if (selectedLocation == "South") {
                binding.cbSouth.isChecked = true
            } else if (selectedLocation == "Lowlands") {
                binding.cbLowlands.isChecked = true
            }
            if (post!!.photoUrl.isEmpty()) {
                binding.postImageView.visibility = View.GONE
            } else {
                binding.postImageView.visibility = View.VISIBLE
                Picasso.get().load(post!!.photoUrl)
                    .placeholder(R.drawable.placeholder)
                    .resize(120, 100)
                    .centerCrop()
                    .into(binding.postImageView)
            }

            binding.postEditText.setText(post!!.description)
        }

    }

}
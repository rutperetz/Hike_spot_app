package com.hikespot.app.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.hikespot.app.R
import com.hikespot.app.databinding.FragmentEditProfileBinding
import com.hikespot.app.model.User
import com.hikespot.app.repository.AuthRepository
import com.hikespot.app.utils.UserManager
import com.hikespot.app.view.activities.MainActivity
import com.hikespot.app.viewmodel.AuthViewModel
import com.squareup.picasso.Picasso


class EditProfileFragment : Fragment() {
    private lateinit var binding:FragmentEditProfileBinding
    private var user: User? =null
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authRepository: AuthRepository
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (user == null){
            user = UserManager.getUser()
        }
        authRepository = AuthRepository()
        authViewModel = AuthViewModel(authRepository)

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.profileImage.setImageURI(uri)
            } else {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayUserInfo()

        binding.changeProfileButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.buttonUpdate.setOnClickListener {
            binding.buttonUpdate.isEnabled = false
            if (selectedImageUri != null){
                binding.changeProfileButton.isEnabled = false
                authViewModel.updateProfileImage(user!!.id, selectedImageUri.toString())
                selectedImageUri = null
            }

           authViewModel.updateUsername(user!!.id,binding.editProfileUsername.text.toString())
           Handler(Looper.getMainLooper()).postDelayed({
               binding.changeProfileButton.isEnabled = true
               binding.buttonUpdate.isEnabled = true
               Toast.makeText(requireActivity(),"Profile Update Successfully!",Toast.LENGTH_SHORT).show()
               authViewModel.loadUser()
           },3000)
        }

        binding.buttonLogout.setOnClickListener {
            authViewModel.logout()
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            },2000)
        }
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
            binding.editProfileUsername.setText(user!!.username)
        }
    }

}
package com.hikespot.app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hikespot.app.R
import com.hikespot.app.databinding.FragmentPersonalFeedBinding
import com.hikespot.app.model.User
import com.hikespot.app.utils.UserManager
import com.squareup.picasso.Picasso


class PersonalFeedFragment : Fragment() {
    private lateinit var binding:FragmentPersonalFeedBinding

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

        displayUserInfo()

        binding.settingsProfileButton.setOnClickListener {
         findNavController().navigate(R.id.action_personal_feed_to_edit_profile)
        }
    }

    override fun onResume() {
        super.onResume()
        displayUserInfo()
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

}
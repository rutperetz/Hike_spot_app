package com.hikespot.app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.hikespot.app.R
import com.hikespot.app.databinding.FragmentSignUpBinding
import com.hikespot.app.repository.AuthRepository
import com.hikespot.app.viewmodel.AuthViewModel


class SignUpFragment : Fragment() {

    private lateinit var binding:FragmentSignUpBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authRepository = AuthRepository()
        authViewModel = AuthViewModel(authRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.signUpButton.setOnClickListener {
            val username = binding.signUpUsername.text.toString()
            val email = binding.signUpEmail.text.toString()
            val password = binding.signUpPassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()){
                Toast.makeText(requireActivity(),"All fields are required!", Toast.LENGTH_SHORT).show()
            }
            else{
                binding.signUpProgressBar.visibility = View.VISIBLE
                binding.signUpButton.isEnabled = false
                authViewModel.signUp(username,email,password)
            }
        }

        authViewModel.authResult.observe(requireActivity()) { user ->
            binding.signUpProgressBar.visibility = View.GONE
            binding.signUpButton.isEnabled = true
            if (user != null) {
                Toast.makeText(requireActivity(), "Sign Up as ${user.email}", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.error.observe(requireActivity()) { errorMsg ->
            binding.signUpProgressBar.visibility = View.GONE
            binding.signUpEmail.isEnabled = true
            Toast.makeText(requireActivity(), "Error: $errorMsg", Toast.LENGTH_SHORT).show()
        }
    }

}
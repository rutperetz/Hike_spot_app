package com.hikespot.app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.hikespot.app.R
import com.hikespot.app.databinding.FragmentLoginBinding
import com.hikespot.app.repository.AuthRepository
import com.hikespot.app.viewmodel.AuthViewModel


class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(requireActivity(),"Email and Password fields are required!",Toast.LENGTH_SHORT).show()
            }
            else{
                binding.loginProgressBar.visibility = View.VISIBLE
                binding.loginButton.isEnabled = false
                authViewModel.login(email,password)
            }
        }

        authViewModel.authResult.observe(requireActivity()) { user ->
            binding.loginProgressBar.visibility = View.GONE
            binding.loginButton.isEnabled = true
            if (user != null) {
                Toast.makeText(requireActivity(), "Login as ${user.email}", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.error.observe(requireActivity()) { errorMsg ->
            binding.loginProgressBar.visibility = View.GONE
            binding.loginButton.isEnabled = true
            Toast.makeText(requireActivity(), "Error: $errorMsg", Toast.LENGTH_SHORT).show()
        }
    }

}
package com.hikespot.app.view.activities

import android.os.Bundle
import android.view.View
<<<<<<< HEAD
import android.widget.Toast
=======
>>>>>>> 7739a9f829ca4d5bee54fc9ce8c4dcc9e616bd10
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hikespot.app.R
import com.hikespot.app.databinding.ActivityMainBinding
<<<<<<< HEAD
import com.hikespot.app.repository.AuthRepository
import com.hikespot.app.viewmodel.AuthViewModel
=======
>>>>>>> 7739a9f829ca4d5bee54fc9ce8c4dcc9e616bd10

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var navController: NavController
<<<<<<< HEAD
    private lateinit var authViewModel: AuthViewModel
=======
>>>>>>> 7739a9f829ca4d5bee54fc9ce8c4dcc9e616bd10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

<<<<<<< HEAD
        val authRepository = AuthRepository()
        authViewModel = AuthViewModel(authRepository)

        setUpToolbar()
        setUpNavController()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.popBackStack(R.id.feed, false)
                    navController.navigate(R.id.feed)
                    true
                }
                R.id.navigation_search -> {
                    navController.popBackStack(R.id.search, false)
                    navController.navigate(R.id.search)
                    true
                }
                R.id.navigation_add -> {
                    navController.popBackStack(R.id.new_post, false)
                    navController.navigate(R.id.new_post)
                    true
                }
                R.id.navigation_profile -> {
                    navController.popBackStack(R.id.personal_feed, false)
                    navController.navigate(R.id.personal_feed)
                    true
                }
                else -> false
            }
        }
=======
        setUpToolbar()
        setUpNavController()

>>>>>>> 7739a9f829ca4d5bee54fc9ce8c4dcc9e616bd10
    }

    private fun setUpToolbar(){
        setSupportActionBar(binding.toolbar)
<<<<<<< HEAD
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
        }
=======
>>>>>>> 7739a9f829ca4d5bee54fc9ce8c4dcc9e616bd10
    }

    private fun setUpNavController(){

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.signUpFragment -> {
                    binding.bottomNavigation.visibility = View.GONE
                    binding.appBar.visibility = View.GONE
                }
                else -> {
<<<<<<< HEAD
                    authViewModel.loadUser()
                    authViewModel.userLiveData.observe(this) { user ->
                        if (user != null) {
//                            Toast.makeText(this, "Welcome ${user.username}", Toast.LENGTH_SHORT).show()
                        }
                    }
=======
>>>>>>> 7739a9f829ca4d5bee54fc9ce8c4dcc9e616bd10
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.appBar.visibility = View.VISIBLE
                }
            }
        }
<<<<<<< HEAD

        val isUserLogged = authViewModel.getCurrentUser()
        val startDestination = if (isUserLogged == null) R.id.loginFragment else R.id.feed
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
=======
>>>>>>> 7739a9f829ca4d5bee54fc9ce8c4dcc9e616bd10
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

}
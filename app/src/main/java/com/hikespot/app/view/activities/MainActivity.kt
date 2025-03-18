package com.hikespot.app.view.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hikespot.app.R
import com.hikespot.app.databinding.ActivityMainBinding
import com.hikespot.app.repository.AuthRepository
import com.hikespot.app.viewmodel.AuthViewModel
import com.hikespot.app.viewmodel.WeatherViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel
    private val weatherViewModel: WeatherViewModel by viewModels()
    val apiKey = "e572674eea9c73a4e16cf8e04e675e9a" // Replace with your API key
    val city = "Tel Aviv"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }

    private fun setUpToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
        }
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
                    authViewModel.loadUser()
                    authViewModel.userLiveData.observe(this) { user ->
                        if (user != null) {
//                            Toast.makeText(this, "Welcome ${user.username}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.appBar.visibility = View.VISIBLE
                }
            }
        }

        val isUserLogged = authViewModel.getCurrentUser()
        val startDestination = if (isUserLogged == null) R.id.loginFragment else R.id.feed
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        displayWeatherDetails()
    }

    private fun displayWeatherDetails(){
        weatherViewModel.fetchWeather(city, apiKey)
        weatherViewModel.weatherData.observe(this, Observer { weather ->
            weather?.let {
                binding.weatherTextView.setTextColor(ContextCompat.getColor(this@MainActivity,R.color.green))
                binding.weatherTextView.text = "City: ${it.name} : Temp: ${it.main.temp}°C"
            } ?: run {
                binding.weatherTextView.setTextColor(ContextCompat.getColor(this@MainActivity,R.color.red))
                binding.weatherTextView.text = "No Weather data, Check API KEY"
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

}
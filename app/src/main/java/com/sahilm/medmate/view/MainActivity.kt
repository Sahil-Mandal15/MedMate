package com.sahilm.medmate.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sahilm.medmate.R
import com.sahilm.medmate.auth.AuthClient
import com.sahilm.medmate.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var authClient: AuthClient
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        authClient = AuthClient(this)

        lifecycleScope.launch {
            println("MainActivity, performing CheckLoginState")
            checkLoginState()
        }






        navController.addOnDestinationChangedListener { _, currentDestination, _  ->
            checkForBottomNavBar(currentDestination.id)
        }


    }

    private fun checkForBottomNavBar(currentDestination: Int) {
        when (currentDestination) {
            R.id.landingScreenFragment, R.id.authScreenFragment -> {
                binding.fmBottomNavView.visibility = View.GONE
            }
            else -> {
                binding.fmBottomNavView.visibility = View.VISIBLE
            }
        }
    }

    private suspend fun checkLoginState() {
        try {
            val loginState = authClient.loginState.first()
            println("MainActivity, LoginState - ${loginState.isLoggedIn}")

            val startDestination = if (loginState.isLoggedIn) {
                println("MainActivity, Opening homescreenfragment")
                R.id.homeScreenFragment
            } else {
                println("MainActivity, Opening landingscreenfragmnet")
                R.id.landingScreenFragment
            }
            println("MainActivity, startdestination = $startDestination")

            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
            navGraph.setStartDestination(startDestination)
            navController.graph = navGraph
        } catch (e: Exception) {
            println("MainActivity" + "Exception while checking login state: $e")
            navController.navigate(R.id.landingScreenFragment)
        }
    }
}
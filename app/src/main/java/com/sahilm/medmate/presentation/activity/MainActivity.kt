package com.sahilm.medmate.presentation.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sahilm.medmate.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navHostView = findViewById<FragmentContainerView>(R.id.nav_host_fragment)

        bottomNav.setupWithNavController(navController)

        // Apply window insets: status bar top padding on root, nav bar bottom padding on bottom nav
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Top padding on the fragment container (for status bar)
            navHostView.setPadding(0, systemBars.top, 0, navHostView.paddingBottom)
            // Bottom nav sits above navigation bar
            (bottomNav.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = systemBars.bottom
            bottomNav.requestLayout()
            insets
        }

        // Once bottom nav is laid out, sync fragment container bottom padding to nav bar height
        bottomNav.post {
            navHostView.setPadding(
                navHostView.paddingLeft,
                navHostView.paddingTop,
                navHostView.paddingRight,
                bottomNav.height
            )
        }
    }
}
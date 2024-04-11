package com.application.parkpilot.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.fragment.Map
import com.application.parkpilot.viewModel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

class HomeActivity : AppCompatActivity(R.layout.home) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // object creation and initialization of views

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        val viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val mapFragment = Map()

        setMenuVisibility(bottomNavigationView.menu)

        // load the profile image in search bar if present
        viewModel.loadProfileImage(this, bottomNavigationView.menu.findItem(R.id.buttonProfile))


        // when search bar menu's items clicked
        bottomNavigationView.setOnItemSelectedListener { clickedItem ->
            if (bottomNavigationView.selectedItemId != clickedItem.itemId) {

                when (clickedItem.itemId) {
                    R.id.buttonList -> {
                        startActivity(Intent(this, SpotListActivity::class.java))
                        return@setOnItemSelectedListener true
                    }

                    R.id.buttonProfile -> {
                        viewModel.profile(this)
                        return@setOnItemSelectedListener true
                    }

                    R.id.buttonHistory -> {
                        viewModel.history(this)

                        return@setOnItemSelectedListener true
                    }

                    R.id.buttonMap -> {
                        return@setOnItemSelectedListener true
                    }

                }
            }
            return@setOnItemSelectedListener false
        }
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.frameLayout,mapFragment)
            commit()
        }
    }

    private fun setMenuVisibility(menu: Menu) {
        User.apply {
            when (type) {
                ANONYMOUS -> {
                    menu.findItem(R.id.buttonHistory).isVisible = false
                }

                FINDER -> {

                }
            }
        }
    }
}
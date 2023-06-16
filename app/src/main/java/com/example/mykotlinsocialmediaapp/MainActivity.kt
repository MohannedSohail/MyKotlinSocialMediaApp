package com.example.mykotlinsocialmediaapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mykotlinsocialmediaapp.databinding.ActivityMainBinding
import com.example.mykotlinsocialmediaapp.model.Student
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val addPostFragment = PostFragment()
    private val profileFragment = ProfileFragment()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        addNavigationListener()

    }

    private fun addNavigationListener() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, homeFragment).commit()
        binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {

                    replaceFragment(homeFragment)
                    true
                }
                R.id.add_post -> {

                    replaceFragment(addPostFragment)
                    return@setOnItemSelectedListener true
                }

                R.id.profile -> {

                    replaceFragment(profileFragment)
                    return@setOnItemSelectedListener true
                }


                else -> {
                    false
                }
            }

        }
    }


    private fun replaceFragment(fragment: Fragment) {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()

    }


}


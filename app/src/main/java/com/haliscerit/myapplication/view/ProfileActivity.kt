package com.haliscerit.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.R
import com.haliscerit.myapplication.databinding.ActivityProfileBinding
import com.haliscerit.myapplication.repository.UserRepository
import com.haliscerit.myapplication.viewmodel.ProfileViewModel
import com.haliscerit.myapplication.viewmodel.ViewModelFactory

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val repository = UserRepository(auth)
        viewModel = ViewModelProvider(this, ViewModelFactory(userRepository = repository)).get(ProfileViewModel::class.java)

        setupObservers()
        viewModel.loadUserInfo()
        viewModel.loadQuizResults()

        setupBottomNavigation()
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Profile"
        }
    }

    private fun setupObservers() {
        viewModel.userInfo.observe(this) {
            binding.apply {
                nametext.text = "Name:"
                emailtext.text = "Email:"
                profileName.text = it.child("name").value.toString()
                profileEmail.text = it.child("email").value.toString()
                profileImage.visibility = View.VISIBLE
                process.visibility = View.VISIBLE
                processlayout.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }

        viewModel.quizResults.observe(this) { resultsText ->
            binding.textView3.text = resultsText
        }

        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.apply {
                    progressBar.visibility = View.VISIBLE
                    profileImage.visibility = View.GONE
                    process.visibility = View.GONE
                    processlayout.visibility = View.GONE
                }
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }


    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.logout -> {
                    auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}

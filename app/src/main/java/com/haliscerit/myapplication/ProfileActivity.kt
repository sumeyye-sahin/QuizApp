package com.haliscerit.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title="Profile"

        var bottomNav= binding.bottomNavigationView
        bottomNav.setSelectedItemId(R.id.profile)
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }



    }
    override fun onResume() { // anlamı: uygulama yeniden başlatıldığında çalışır
        super.onResume()  // onResume aktivite yeniden başlatıldığında çalışır


        Firebase.database.reference.child("Users").child(auth.currentUser!!.uid).get().addOnSuccessListener {
            binding.apply {
                progressBar.visibility = View.GONE //görünürlük: GONE, görünmez
            }
            binding.nametext.text="Name:"
            binding.emailtext.text="Email:"
            binding.profileImage.visibility = View.VISIBLE
            binding.profileName.text = it.child("name").value.toString()
            binding.profileEmail.text = it.child("email").value.toString()
            //binding.password.text = it.child("password").value.toString()
        }
    }
}
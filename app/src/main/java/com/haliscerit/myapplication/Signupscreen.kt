package com.haliscerit.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.databinding.ActivitySignupscreenBinding
import com.haliscerit.myapplication.model.User

class Signupscreen : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivitySignupscreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title=""

        auth= Firebase.auth


    }
    fun signUpClicked (view: View) {

        val name = binding.name.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (email.equals("") || password.equals("") || name.equals("")) {
            Toast.makeText(this, "Name, email and password cannot be empty!", Toast.LENGTH_LONG).show()
        }
        else {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {

                val currentUser = auth.currentUser
                val currentUserDb = Firebase.database.reference.child("Users").child(currentUser!!.uid)
                val user = User(name, email, password)
                currentUserDb.setValue(user)

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}
package com.haliscerit.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        auth= Firebase.auth


        val currentUser= auth.currentUser
        if(currentUser != null){
            val intent= Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signupClick (view: View){

        val intent= Intent(this, Signupscreen::class.java)
        startActivity(intent)
        // finish() // geri dönüşü olmayan bir aktiviteye geçiş yapmak için kullanılır (bu aktiviteyi kapatır)
    }

    fun singinClick (view: View){
        val email= binding.email.text.toString()
        val password= binding.password.text.toString()

        if(email.equals("") || password.equals("")){
            Toast.makeText(this,"Email and password cannot be empty!",Toast.LENGTH_LONG).show()
        } else {
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent= Intent(this, Splashscreen::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener{
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }
}
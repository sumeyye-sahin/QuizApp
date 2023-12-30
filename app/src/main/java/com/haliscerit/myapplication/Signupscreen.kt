package com.haliscerit.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.databinding.ActivitySignupscreenBinding
import com.haliscerit.myapplication.model.User

class Signupscreen : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivitySignupscreenBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title=""


        auth= Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this, gso)

    }

    fun buttongoogle(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)

        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if(account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {

                // Google ile başarılı bir şekilde giriş yapıldı
                val user = auth.currentUser
                val currentUserDb = Firebase.database.reference.child("Users").child(user!!.uid)

                // Kullanıcı adını ve e-posta adresini alıp Firebase veritabanına kaydet
                val userName = account.displayName
                val userEmail = account.email
                val userModel = User(userName, userEmail)
                currentUserDb.setValue(userModel)

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun  loginClick (view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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
                val user = User(name, email)
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
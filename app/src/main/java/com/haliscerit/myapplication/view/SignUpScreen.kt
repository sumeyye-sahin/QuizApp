package com.haliscerit.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.R
import com.haliscerit.myapplication.databinding.ActivitySignupscreenBinding
import com.haliscerit.myapplication.repository.AuthRepository
import com.haliscerit.myapplication.repository.UserRepository
import com.haliscerit.myapplication.viewmodel.AuthViewModel
import com.haliscerit.myapplication.viewmodel.ViewModelFactory

class SignUpScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySignupscreenBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        auth = Firebase.auth

        val authRepository = AuthRepository(auth, GoogleSignIn.getClient(this, createGoogleSignInOptions()))
        val userRepository = UserRepository(auth)
        viewModel = ViewModelProvider(this, ViewModelFactory(authRepository = authRepository, userRepository = userRepository)).get(AuthViewModel::class.java)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttongoogle.setOnClickListener {
            val signInIntent = viewModel.getGoogleSignInIntent()
            googleSignInLauncher.launch(signInIntent)
        }

        binding.signin.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.signUpWithEmail(name, email, password,
                    onSuccess = {
                        navigateToHome()
                    },
                    onFailure = { errorMessage ->
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    })
            } else {
                Toast.makeText(this, "Name, email and password cannot be empty!", Toast.LENGTH_LONG).show()
            }
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleGoogleSignInResult(task)
    }

    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(Exception::class.java)
            val idToken = account?.idToken

            if (idToken != null) {
                viewModel.signInWithGoogle(idToken,
                    onSuccess = {
                        navigateToHome()
                    },
                    onFailure = { errorMessage ->
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    })
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Google sign in failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun createGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

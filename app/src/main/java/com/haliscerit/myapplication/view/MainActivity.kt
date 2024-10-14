package com.haliscerit.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.haliscerit.myapplication.R
import com.haliscerit.myapplication.databinding.ActivityMainBinding
import com.haliscerit.myapplication.repository.AuthRepository
import com.haliscerit.myapplication.repository.UserRepository
import com.haliscerit.myapplication.viewmodel.AuthViewModel
import com.haliscerit.myapplication.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AuthViewModel

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        handleGoogleSignInResult(result.resultCode, result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()
        val googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )

        val authRepository = AuthRepository(auth, googleSignInClient)
        val userRepository = UserRepository(auth)
        viewModel = ViewModelProvider(this, ViewModelFactory(authRepository, userRepository)).get(AuthViewModel::class.java)

        viewModel.checkCurrentUser {
            navigateToHome()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    fun buttongoogle(view: View) {
        val signInIntent = viewModel.getGoogleSignInIntent()
        googleSignInLauncher.launch(signInIntent)
    }

    private fun handleGoogleSignInResult(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                viewModel.signInWithGoogle(account.idToken!!, {
                    navigateToHome()
                }, { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                })
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign In Failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signupClick(view: View) {
        val intent = Intent(this, SignUpScreen::class.java)
        startActivity(intent)
    }

    fun singinClick(view: View) {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty!", Toast.LENGTH_LONG).show()
        } else {
            viewModel.signInWithEmail(email, password, {
                navigateToHome()
            }, { error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            })
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

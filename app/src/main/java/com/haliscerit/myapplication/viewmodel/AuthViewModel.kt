package com.haliscerit.myapplication.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.haliscerit.myapplication.repository.AuthRepository
import com.haliscerit.myapplication.repository.UserRepository

class AuthViewModel(private val repository: AuthRepository, userRepository: UserRepository) : ViewModel() {

    fun checkCurrentUser(onUserLoggedIn: () -> Unit) {
        if (repository.getCurrentUser() != null) {
            onUserLoggedIn()
        }
    }

    fun getGoogleSignInIntent(): Intent {
        return repository.getGoogleSignInIntent()
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        repository.firebaseAuthWithGoogle(idToken, {
            saveUserToDatabase()
            onSuccess()
        }, onFailure)
    }

    fun signUpWithEmail(name: String, email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        repository.signUpWithEmail(name, email, password, {
            saveUserToDatabase(name, email)
            onSuccess()
        }, onFailure)
    }

    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        repository.signInWithEmail(email, password, onSuccess, onFailure)
    }

    private fun saveUserToDatabase(name: String? = null, email: String? = null) {
        val user = repository.getCurrentUser()
        if (user != null) {
            repository.saveUserToDatabase(name ?: user.displayName, email ?: user.email)
        }
    }
}

package com.haliscerit.myapplication.repository

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.haliscerit.myapplication.model.User

class AuthRepository(private val auth: FirebaseAuth, private val googleSignInClient: GoogleSignInClient) {

    private val databaseRef = FirebaseDatabase.getInstance().reference.child("Users")

    fun getCurrentUser() = auth.currentUser

    fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent

    fun firebaseAuthWithGoogle(idToken: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                saveUserToDatabase()
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Error") }
    }

    fun signUpWithEmail(name: String, email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                saveUserToDatabase(name, email)
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Error") }
    }

    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Error") }
    }

    fun saveUserToDatabase(name: String? = null, email: String? = null) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val userName = name ?: user.displayName
            val userEmail = email ?: user.email
            val userModel = User(userName, userEmail)

            databaseRef.child(userId).setValue(userModel)
        }
    }
}

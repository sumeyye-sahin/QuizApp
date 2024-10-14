package com.haliscerit.myapplication.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.model.User

class UserRepository(private val auth: FirebaseAuth = Firebase.auth) {
    private val database: FirebaseDatabase = Firebase.database

    private val userRef by lazy {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.reference.child("Users").child(userId)
        } else {
            null
        }
    }

    fun getUserInfo(onSuccess: (DataSnapshot) -> Unit, onFailure: (DatabaseError) -> Unit) {
        userRef?.get()?.addOnSuccessListener(onSuccess)?.addOnFailureListener {
            onFailure(DatabaseError.fromException(it))
        }
    }

    fun getQuizResults(onSuccess: (DataSnapshot) -> Unit, onFailure: (DatabaseError) -> Unit) {
        userRef?.child("QuizResults")?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onSuccess(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error)
            }
        })
    }


}

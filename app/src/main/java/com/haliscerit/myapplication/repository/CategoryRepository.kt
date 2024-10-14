package com.haliscerit.myapplication.repository

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class CategoryRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun fetchQuestionCounts(categories: List<String>): Task<List<QuerySnapshot>> {
        val tasks = categories.map { category ->
            firestore.collection("Questions")
                .document(category)
                .collection("question1")
                .get()
        }
        return Tasks.whenAllSuccess(tasks)
    }

    fun fetchUserQuizResults(userId: String, onComplete: (DataSnapshot) -> Unit, onError: (String) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("QuizResults")
        userRef.get().addOnSuccessListener(onComplete).addOnFailureListener { onError(it.message ?: "Error") }
    }
}

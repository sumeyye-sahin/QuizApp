package com.haliscerit.myapplication.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.model.Question

class QuizRepository(private val auth: FirebaseAuth) {
    private val firestore = FirebaseFirestore.getInstance()
    private val userRef = Firebase.database.reference.child("Users").child(auth.currentUser!!.uid)

    fun fetchQuestions(category: String, onSuccess: (List<Question>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("Questions").document(category).collection("question1").get()
            .addOnSuccessListener { snapshot ->
                val questions = snapshot.documents.mapNotNull { it.toObject(Question::class.java) }
                onSuccess(questions)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateScore(category: String, questionId: String, isCorrect: Boolean) {
        val quizResultsRef = userRef.child("QuizResults").child(category).child(questionId)
        quizResultsRef.child("currentiscorrect").setValue(isCorrect)
        val scoreType = if (isCorrect) "correctCount" else "wrongCount"

        quizResultsRef.child(scoreType).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentCount = snapshot.value as? Long ?: 0
                quizResultsRef.child(scoreType).setValue(currentCount + 1)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to update score: ${error.message}")
            }
        })
    }

    fun getQuizResults(category: String, onSuccess: (Long, Long) -> Unit, onFailure: (DatabaseError) -> Unit) {
        val currentCategoryRef = userRef.child("QuizResults").child(category)
        currentCategoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var correctCount = 0L
                var wrongCount = 0L

                snapshot.children.forEach { questionSnapshot ->
                    val isCorrect = questionSnapshot.child("currentiscorrect").getValue(Boolean::class.java) ?: false
                    if (isCorrect) correctCount++ else wrongCount++
                }
                onSuccess(correctCount, wrongCount)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error)
            }
        })
    }
}

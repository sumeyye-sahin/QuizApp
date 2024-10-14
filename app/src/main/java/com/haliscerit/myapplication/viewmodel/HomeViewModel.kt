package com.haliscerit.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.haliscerit.myapplication.repository.CategoryRepository

class HomeViewModel(private val repository: CategoryRepository) : ViewModel() {
    private val _totalQuestionCount = MutableLiveData<Int>()
    val totalQuestionCount: LiveData<Int> get() = _totalQuestionCount

    private val _totalQuestionsAttempted = MutableLiveData<Int>()
    val totalQuestionsAttempted: LiveData<Int> get() = _totalQuestionsAttempted

    private val _successRate = MutableLiveData<Int>()
    val successRate: LiveData<Int> get() = _successRate

    private val categories = listOf(
        "Network Communications And Connectivity",
        "Layer 2 Data Link",
        "Ethernet Concepts",
        "Network's Models And Protocols",
        "Numbering Systems",
        "Switching \"Ethernet Protocol\"",
        "Today’s Networking"
    )

    fun loadQuestionCounts() {
        repository.fetchQuestionCounts(categories).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var total = 0
                task.result?.forEach { querySnapshot ->
                    total += querySnapshot.size()
                }
                _totalQuestionCount.value = total
                updateSuccessRate()
            } else {
                _totalQuestionCount.value = 0
                updateSuccessRate()
            }
        }
    }

    fun loadUserQuizResults(userId: String) {
        repository.fetchUserQuizResults(userId, { snapshot ->
            var attempted = 0
            snapshot.children.forEach { categorySnapshot ->
                categorySnapshot.children.forEach {
                    attempted++
                }
            }
            _totalQuestionsAttempted.value = attempted
            updateSuccessRate()
        }, { errorMessage ->
        })
    }

    private fun updateSuccessRate() {
        val totalQuestions = _totalQuestionCount.value ?: 0
        val attemptedQuestions = _totalQuestionsAttempted.value ?: 0
        _successRate.value = if (totalQuestions > 0) {
            (attemptedQuestions * 100) / totalQuestions
        } else {
            0
        }
    }

}

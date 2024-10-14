package com.haliscerit.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.haliscerit.myapplication.repository.UserRepository

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userInfo = MutableLiveData<DataSnapshot>()
    val userInfo: LiveData<DataSnapshot> get() = _userInfo

    private val _quizResults = MutableLiveData<String>()
    val quizResults: LiveData<String> get() = _quizResults

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun loadUserInfo() {
        _loading.value = true
        repository.getUserInfo(onSuccess = {
            _userInfo.value = it
            _loading.value = false
        }, onFailure = {
            _loading.value = false
        })
    }

    fun loadQuizResults() {
        _loading.value = true
        repository.getQuizResults(onSuccess = { snapshot ->
            var text = ""
            for (categorySnapshot in snapshot.children) {
                val category = categorySnapshot.key
                var correctCount = 0L
                var wrongCount = 0L
                categorySnapshot.children.forEach { questionSnapshot ->
                    val isCorrect = questionSnapshot.child("currentiscorrect").getValue(Boolean::class.java) ?: false
                    if (isCorrect) correctCount++ else wrongCount++
                }
                text += "$category: \nCorrect Count: $correctCount\nWrong Count: $wrongCount\n\n"
            }
            _quizResults.value = text
            _loading.value = false
        }, onFailure = {
            _loading.value = false
        })
    }
}

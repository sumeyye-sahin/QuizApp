package com.haliscerit.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haliscerit.myapplication.model.Question
import com.haliscerit.myapplication.repository.QuizRepository

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> get() = _questions

    private val _quizResults = MutableLiveData<Pair<Long, Long>>()
    val quizResults: LiveData<Pair<Long, Long>> get() = _quizResults

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun loadQuestions(category: String) {
        _loading.value = true
        repository.fetchQuestions(category, onSuccess = { questionList ->
            _questions.value = questionList
            _loading.value = false
        }, onFailure = {
            _loading.value = false
        })
    }

    fun updateScore(category: String, questionId: String, isCorrect: Boolean) {
        repository.updateScore(category, questionId, isCorrect)
    }

    fun evaluateQuiz(category: String) {
        repository.getQuizResults(category, onSuccess = { correctCount, wrongCount ->
            _quizResults.value = Pair(correctCount, wrongCount)
        }, onFailure = {
            println("Failed to retrieve quiz results: ${it.message}")
        })
    }
}

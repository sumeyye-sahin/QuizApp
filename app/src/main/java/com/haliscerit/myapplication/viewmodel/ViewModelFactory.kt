package com.haliscerit.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.haliscerit.myapplication.repository.AuthRepository
import com.haliscerit.myapplication.repository.CategoryRepository
import com.haliscerit.myapplication.repository.QuizRepository
import com.haliscerit.myapplication.repository.UserRepository

class ViewModelFactory(
    private val authRepository: AuthRepository? = null,
    private val userRepository: UserRepository? = null,
    private val categoryRepository: CategoryRepository? = null,
    private val quizRepository: QuizRepository? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) && authRepository != null && userRepository != null -> {
                AuthViewModel(authRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) && userRepository != null -> {
                ProfileViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) && categoryRepository != null -> {
                HomeViewModel(categoryRepository) as T
            }
            modelClass.isAssignableFrom(QuizViewModel::class.java) && quizRepository != null -> {
                QuizViewModel(quizRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class or missing repository")
        }
    }
}


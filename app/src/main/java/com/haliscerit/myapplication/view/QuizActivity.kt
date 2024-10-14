package com.haliscerit.myapplication.view

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.databinding.ActivityQuizBinding
import com.haliscerit.myapplication.model.Question
import com.haliscerit.myapplication.repository.QuizRepository
import com.haliscerit.myapplication.viewmodel.QuizViewModel
import com.haliscerit.myapplication.viewmodel.ViewModelFactory

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: QuizViewModel

    private var currentQuestionIndex = 0
    private lateinit var questionList: List<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val repository = QuizRepository(auth)
        viewModel = ViewModelProvider(this, ViewModelFactory(quizRepository = repository)).get(QuizViewModel::class.java)

        val category = intent.getStringExtra("questionType") ?: ""
        setupObservers()
        viewModel.loadQuestions(category)

        binding.option1.setOnClickListener { checkAnswer(binding.option1) }
        binding.option2.setOnClickListener { checkAnswer(binding.option2) }
        binding.option3.setOnClickListener { checkAnswer(binding.option3) }
        binding.option4.setOnClickListener { checkAnswer(binding.option4) }
        binding.back.setOnClickListener { goBackToPreviousQuestion() }
    }

    private fun setupObservers() {
        viewModel.questions.observe(this) { questions ->
            questionList = questions
            updateQuestionUI()
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
            val visibility = if (!isLoading) View.VISIBLE else View.GONE

            binding.question.visibility = visibility
            binding.option1.visibility = visibility
            binding.option2.visibility = visibility
            binding.option3.visibility = visibility
            binding.option4.visibility = visibility
            binding.next.visibility = visibility
            binding.back.visibility = visibility
            binding.sorusirasi.visibility = visibility
            binding.line.visibility = visibility
            binding.expLayout.visibility = View.GONE
        }

        viewModel.quizResults.observe(this) { result ->
            showQuizResults(result.first, result.second)
        }
    }


    private fun checkAnswer(selectedButton: Button) {
        val question = questionList[currentQuestionIndex]
        val isCorrect = selectedButton.text == question.ans
        val category = intent.getStringExtra("questionType") ?: ""

        viewModel.updateScore(category, question.id.toString(), isCorrect)
        setButtonColor(selectedButton, if (isCorrect) Color.GREEN else Color.RED)

        if (!isCorrect) {
            binding.expLayout.visibility = View.VISIBLE
            binding.expText.text = question.exp?.replace("/n", "\n") ?: ""
            question.ans?.let { highlightCorrectAnswer(it) }
        } else {
            binding.expLayout.visibility = View.GONE
        }

        setButtonClickable(false)

        binding.next.setOnClickListener {
            if (currentQuestionIndex < questionList.size - 1) {
                currentQuestionIndex++
                updateQuestionUI()
                binding.expLayout.visibility = View.GONE
            } else {
                viewModel.evaluateQuiz(category)
            }
        }
    }

    private fun highlightCorrectAnswer(correctAnswer: String) {
        when (correctAnswer) {
            binding.option1.text -> setButtonColor(binding.option1, Color.GREEN, Color.WHITE)
            binding.option2.text -> setButtonColor(binding.option2, Color.GREEN, Color.WHITE)
            binding.option3.text -> setButtonColor(binding.option3, Color.GREEN, Color.WHITE)
            binding.option4.text -> setButtonColor(binding.option4, Color.GREEN, Color.WHITE)
        }
    }

    private fun updateQuestionUI() {
        if (currentQuestionIndex < questionList.size) {
            val question = questionList[currentQuestionIndex]
            binding.question.text = question.question?.replace("/n", "\n")
            binding.option1.text = question.option1
            binding.option2.text = question.option2
            binding.option3.text = question.option3
            binding.option4.text = question.option4
            binding.sorusirasi.text = "Question - ${currentQuestionIndex + 1}"
            binding.expText.text = question.exp?.replace("/n", "\n") ?: ""

            resetButtonColors()
            setButtonClickable(true)
        }
    }
    private fun setButtonClickable(clickable: Boolean) {
        binding.option1.isClickable = clickable
        binding.option2.isClickable = clickable
        binding.option3.isClickable = clickable
        binding.option4.isClickable = clickable
    }

    private fun resetButtonColors() {
        setButtonColor(binding.option1, Color.TRANSPARENT, Color.BLACK)
        setButtonColor(binding.option2, Color.TRANSPARENT, Color.BLACK)
        setButtonColor(binding.option3, Color.TRANSPARENT, Color.BLACK)
        setButtonColor(binding.option4, Color.TRANSPARENT, Color.BLACK)
    }

    private fun setButtonColor(button: Button, backgroundColor: Int, textColor: Int) {
        button.setBackgroundColor(backgroundColor)
        button.setTextColor(textColor)
    }


    private fun showQuizResults(correctCount: Long, wrongCount: Long) {
        AlertDialog.Builder(this).apply {
            setTitle("Quiz Results")
            setMessage("Correct: $correctCount\nWrong: $wrongCount")
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }


    private fun goBackToPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
            updateQuestionUI()
        } else {
            finish()
        }
    }

    private fun setButtonColor(button: Button, color: Int) {
        button.setBackgroundColor(color)
    }
}

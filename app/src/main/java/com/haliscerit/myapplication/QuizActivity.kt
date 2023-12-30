package com.haliscerit.myapplication

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.databinding.ActivityQuizBinding
import com.haliscerit.myapplication.model.Question

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var questionList: ArrayList<Question>
    var  currentQuestion = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityQuizBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title=""

        questionList= ArrayList<Question>()

        val image= intent.getIntExtra("imageView3",0)
        binding.imageView3.setImageResource(image)

        val catText= intent.getStringExtra("questionType")
        binding.questionType.text= catText

        Firebase.firestore.collection("Questions")
            .document(catText.toString())
            .collection("question1")
            .get().addOnSuccessListener {
                questionData->
                questionList.clear()
                for (data in questionData.documents){
                    var question: Question?= data.toObject(Question::class.java)
                    questionList.add(question!!)
                }

               if(questionList.size >0){
                   binding.question.text= questionList.get(currentQuestion).question
                   binding.option1.text= questionList.get(currentQuestion).option1
                   binding.option2.text= questionList.get(currentQuestion).option2
                   binding.option3.text= questionList.get(currentQuestion).option3
                   binding.option4.text= questionList.get(currentQuestion).option4}
            }.addOnSuccessListener {
                binding.progressBar2.visibility= android.view.View.GONE
                binding.questionType.visibility= android.view.View.VISIBLE
                binding.question.visibility= android.view.View.VISIBLE
                binding.option1.visibility= android.view.View.VISIBLE
                binding.option2.visibility= android.view.View.VISIBLE
                binding.option3.visibility= android.view.View.VISIBLE
                binding.option4.visibility= android.view.View.VISIBLE
                binding.next.visibility= android.view.View.VISIBLE
                binding.imageView3.visibility= android.view.View.VISIBLE
                binding.textView7.visibility= android.view.View.VISIBLE
            }

        binding.option1.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option1.text.toString())
        }
        binding.option2.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option2.text.toString())
        }
         binding.option3.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option3.text.toString())
        }
        binding.option4.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option4.text.toString())
        }




    }

    // doğru ve yanlış cevap kısımlarını veritabanına kayıt etmeliyiz
// doğru ve yanlış cevapları veritabanından çekip ekrana yazdırmalıyız
    private fun nextQuestionAndScoreUpdate(selectedOption: String) {
        val correctAnswer = questionList[currentQuestion].ans
        val isCorrect = selectedOption == correctAnswer


        // Doğru ve yanlış sayıları güncelle
        if (isCorrect) {
            // Doğru cevap
            updateScore("correctCount")
        } else {
            // Yanlış cevap
            updateScore("wrongCount")
        }

        // Renkleri ayarla
        if (correctAnswer != null) {
            setOptionButtonColor(binding.option1, correctAnswer, selectedOption)
        }
        if (correctAnswer != null) {
            setOptionButtonColor(binding.option2, correctAnswer, selectedOption)
        }
        if (correctAnswer != null) {
            setOptionButtonColor(binding.option3, correctAnswer, selectedOption)
        }
        if (correctAnswer != null) {
            setOptionButtonColor(binding.option4, correctAnswer, selectedOption)
        }

        // Butonları tıklanabilir yap
        setButtonClickable(false)

        binding.next.setOnClickListener {
            // Sonraki soruya geç
            currentQuestion++
            resetButtonColors()
            setButtonClickable(true)

            if (currentQuestion >= questionList.size) {
                setButtonClickable(false)
                Toast.makeText(this, "You have reached the end", Toast.LENGTH_SHORT).show()
            } else {
                // Soruları güncelle
                updateQuestionUI()
            }
        }
    }

    private fun updateScore(scoreType: String) {
        val user = Firebase.auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val userRef = Firebase.database.reference.child("Users").child(userId)
            val quizResultsRef = userRef.child("QuizResults")

            quizResultsRef.child(scoreType).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var currentCount = snapshot.value as? Long ?: 0
                    currentCount++
                    quizResultsRef.child(scoreType).setValue(currentCount)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("QuizActivity", "Failed to update score: ${error.message}")
                }
            })
        }
    }

    private fun updateQuestionUI() {
        binding.question.text = questionList[currentQuestion].question
        binding.option1.text = questionList[currentQuestion].option1
        binding.option2.text = questionList[currentQuestion].option2
        binding.option3.text = questionList[currentQuestion].option3
        binding.option4.text = questionList[currentQuestion].option4
    }
 /*   private fun nextQuestionAndScoreUpdate(selectedOption:String) {
        val correctAnswer = questionList.get(currentQuestion).ans

        if (selectedOption == correctAnswer) {

            // The answer is correct, change the color to green
            setOptionButtonColor(binding.option1, correctAnswer,selectedOption)
            setOptionButtonColor(binding.option2, correctAnswer,selectedOption)
            setOptionButtonColor(binding.option3, correctAnswer,selectedOption)
            setOptionButtonColor(binding.option4, correctAnswer,selectedOption)
        } else {
            // The answer is incorrect, change the color to red
            if (correctAnswer != null) {
                setOptionButtonColor(binding.option1, correctAnswer, selectedOption)
                setOptionButtonColor(binding.option2, correctAnswer, selectedOption)
                setOptionButtonColor(binding.option3, correctAnswer, selectedOption)
                setOptionButtonColor(binding.option4, correctAnswer, selectedOption)}
        }
        setButtonClickable(false)

        binding.next.setOnClickListener {
            currentQuestion++
            resetButtonColors()
            setButtonClickable(true)
            if (currentQuestion>=questionList.size){
                setButtonClickable(false)
                Toast.makeText(this,"You have reached to end", Toast.LENGTH_SHORT).show()


            }
            else{

                binding.question.text= questionList.get(currentQuestion).question
                binding.option1.text= questionList.get(currentQuestion).option1
                binding.option2.text= questionList.get(currentQuestion).option2
                binding.option3.text= questionList.get(currentQuestion).option3
                binding.option4.text= questionList.get(currentQuestion).option4

            }

        }
       *//* currentQuestion++
        if (currentQuestion>=questionList.size){

            Toast.makeText(this,"You have reached to end", Toast.LENGTH_SHORT).show()
        }
        else{

        binding.question.text= questionList.get(currentQuestion).question
        binding.option1.text= questionList.get(currentQuestion).option1
        binding.option2.text= questionList.get(currentQuestion).option2
        binding.option3.text= questionList.get(currentQuestion).option3
        binding.option4.text= questionList.get(currentQuestion).option4

        }
        setButtonClickable(true)*//*
    }*/
    private fun setOptionButtonColor(button: Button, correctAnswer: String, selectedOption: String) {
        if (button.text == correctAnswer) {
            // Change the color of the correct answer button to green
            button.setBackgroundColor(Color.GREEN)
            // Change the text color to white
            button.setTextColor(Color.WHITE)
        } else if (button.text == selectedOption) {
            // Change the color of the incorrect answer button to red
            button.setBackgroundColor(Color.RED)
            // Change the text color to white
            button.setTextColor(Color.WHITE)
        }
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
    private fun setButtonClickable(clickable: Boolean) {
        binding.option1.isClickable = clickable
        binding.option2.isClickable = clickable
        binding.option3.isClickable = clickable
        binding.option4.isClickable = clickable
    }

}
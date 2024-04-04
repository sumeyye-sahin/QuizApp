package com.haliscerit.myapplication

import android.app.AlertDialog
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
    private var currentAlertDialog: AlertDialog? = null
    var  currentQuestion = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityQuizBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        questionList= ArrayList<Question>()
        val catText= intent.getStringExtra("questionType")
        supportActionBar!!.title=catText

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
                   binding.question.text = questionList.get(currentQuestion).question.toString().replace("/n", "\n")
                   binding.option1.text= questionList.get(currentQuestion).option1
                   binding.option2.text= questionList.get(currentQuestion).option2
                   binding.option3.text= questionList.get(currentQuestion).option3
                   binding.option4.text= questionList.get(currentQuestion).option4
                   binding.sorusirasi.text = "Question - ${currentQuestion +1}"
                   binding.expText.text=questionList.get(currentQuestion).exp.toString().replace("/n", "\n")
               }
            }.addOnSuccessListener {
                binding.progressBar2.visibility= android.view.View.GONE
                binding.question.visibility= android.view.View.VISIBLE
                binding.option1.visibility= android.view.View.VISIBLE
                binding.option2.visibility= android.view.View.VISIBLE
                binding.option3.visibility= android.view.View.VISIBLE
                binding.option4.visibility= android.view.View.VISIBLE
                binding.next.visibility= android.view.View.VISIBLE
                binding.back.visibility= android.view.View.VISIBLE
                binding.sorusirasi.visibility= android.view.View.VISIBLE
                binding.line.visibility=android.view.View.VISIBLE
                binding.expLayout.visibility=android.view.View.GONE
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

        binding.back.setOnClickListener {
            goBackToPreviousQuestion()
        }



    }


    // doğru ve yanlış cevap kısımlarını veritabanına kayıt etmeliyiz
// doğru ve yanlış cevapları veritabanından çekip ekrana yazdırmalıyız
    private fun nextQuestionAndScoreUpdate(selectedOption: String) {
        val correctAnswer = questionList[currentQuestion].ans
        val isCorrect = selectedOption == correctAnswer

        // Doğru ve yanlış sayıları güncelle
        if (isCorrect) {            // Doğru cevap
            updateScore("correctCount")
        } else {            // Yanlış cevap
            updateScore("wrongCount")
            binding.expLayout.visibility=android.view.View.VISIBLE
        }
        // Renkleri ayarla
        if (correctAnswer != null) {
            setOptionButtonColor(binding.option1, correctAnswer, selectedOption)        }
        if (correctAnswer != null) {
            setOptionButtonColor(binding.option2, correctAnswer, selectedOption)        }
        if (correctAnswer != null) {
            setOptionButtonColor(binding.option3, correctAnswer, selectedOption)        }
        if (correctAnswer != null) {
            setOptionButtonColor(binding.option4, correctAnswer, selectedOption)        }

        // Butonları tıklanabilir yap
        setButtonClickable(false)

        binding.next.setOnClickListener {
            // Sonraki soruya geç
            currentQuestion++
            resetButtonColors()
            setButtonClickable(true)
            binding.expLayout.visibility=android.view.View.GONE

            if (currentQuestion >= questionList.size) {
                setButtonClickable(false)
                Toast.makeText(this, "You have reached the end", Toast.LENGTH_SHORT).show()
                konuSonuDegerlendirme()
                // toplam yapılan yanlış sayısı ve doğru sayısını buraya yazdıracağım
            } else {
                // Soruları güncelle
                updateQuestionUI()
            }
        }
    }

    private fun updateScore(scoreType: String) {
        val user = Firebase.auth.currentUser
        val userId = user?.uid
        var questionid = questionList[currentQuestion].id.toString()
        var category = questionList[currentQuestion].category.toString()

        if (userId != null) {
            val userRef = Firebase.database.reference.child("Users").child(userId)
            val quizResultsRef = userRef.child("QuizResults").child(category).child(questionid)


            quizResultsRef.child(scoreType).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var currentCount = snapshot.value as? Long ?: 0
                    val currentIsCorrect = scoreType == "correctCount"
                    quizResultsRef.child("currentiscorrect").setValue(currentIsCorrect)
                    quizResultsRef.child(scoreType).setValue(currentCount+1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("QuizActivity", "Failed to update score: ${error.message}")
                }
            })
        }
    }

    private  fun updateQuestionUI() {
        // explanation a da replace eklenecek
        binding.question.text = questionList.get(currentQuestion).question.toString().replace("/n", "\n")
        binding.option1.text= questionList[currentQuestion].option1
        binding.option2.text = questionList[currentQuestion].option2
        binding.option3.text = questionList[currentQuestion].option3
        binding.option4.text = questionList[currentQuestion].option4
        binding.expText.text= questionList[currentQuestion].exp.toString().replace("/n", "\n")
        binding.sorusirasi.text = "Question - ${currentQuestion + 1}"


    }

    private fun setOptionButtonColor(button: Button, correctAnswer: String, selectedOption: String) {
        if (button.text == correctAnswer) {
            // Change the color of the correct answer button to green
            button.setBackgroundColor(Color.parseColor("#0097A7"))
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
    private fun goBackToPreviousQuestion() {
        if (currentQuestion > 0) {
            currentQuestion--
            updateQuestionUI()
            resetButtonColors()
            setButtonClickable(true)
            binding.expLayout.visibility=android.view.View.GONE
        } else {
            // Eğer bir önceki soru yoksa, bu aktiviteyi sonlandırabilirsiniz.
            finish()
        }

    }

    private fun konuSonuDegerlendirme() {
        val user = Firebase.auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val userRef = Firebase.database.reference.child("Users").child(userId)
                .child("QuizResults")
            // Mevcut konu adını alınır
            val currentCategory = intent.getStringExtra("questionType")
            // Mevcut konu adına ait verileri çekilir
            val currentCategoryRef = currentCategory?.let { userRef.child(it) }

            if (currentCategoryRef != null) {
                currentCategoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var correctCount = 0L
                        var wrongCount = 0L

                        for (questionSnapshot in snapshot.children) {
                            val currentIsCorrect = questionSnapshot.child("currentiscorrect").getValue(Boolean::class.java)
                                ?: false

                            // Sorunun durumuna göre doğru veya yanlış sayısını arttır
                            if (currentIsCorrect) {
                                correctCount++
                            } else {
                                wrongCount++
                            }

                            // Ekstra Log ifadeleri ekleyin
                            println("Category: $currentCategory, QuestionID: ${questionSnapshot.key}, IsCorrect: $currentIsCorrect")
                        }
                        // Her bir kategori için doğru ve yanlış sayılarını yazdırın
                        println("Category: $currentCategory, Correct Count: $correctCount, Wrong Count: $wrongCount")

                        // Sonuçları gösteren bir alert dialog oluşturun ve gösterin
                        val builder = AlertDialog.Builder(this@QuizActivity)
                        builder.setTitle("Quiz Result - $currentCategory")
                        builder.setMessage("Correct Count: $correctCount\nWrong Count: $wrongCount")

                        builder.setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }

                        // AlertDialog'ı saklayın
                        currentAlertDialog = builder.create()
                        currentAlertDialog?.show()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("Failed to retrieve quiz results: ${error.message}")
                    }
                })
            }
        }
    }



}
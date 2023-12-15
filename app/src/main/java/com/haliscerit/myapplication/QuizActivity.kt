package com.haliscerit.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.databinding.ActivityQuizBinding
import com.haliscerit.myapplication.model.Question

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var questionList: ArrayList<Question>
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
                   binding.question.text= questionList.get(0).question
                   binding.option1.text= questionList.get(0).option1
                   binding.option2.text= questionList.get(0).option2
                   binding.option3.text= questionList.get(0).option3
                   binding.option4.text= questionList.get(0).option4}
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

    }
}
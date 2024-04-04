package com.haliscerit.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title="Profile"
        konuSonuDegerlendirme()


        var bottomNav= binding.bottomNavigationView
        bottomNav.setSelectedItemId(R.id.profile)
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }



    }
    override fun onResume() { // anlamı: uygulama yeniden başlatıldığında çalışır
        super.onResume()  // onResume aktivite yeniden başlatıldığında çalışır

        Firebase.database.reference.child("Users").child(auth.currentUser!!.uid).get().addOnSuccessListener {
            binding.apply {
                progressBar.visibility = View.GONE //görünürlük: GONE, görünmez
            }
            binding.nametext.text="Name:"
            binding.emailtext.text="Email:"
            binding.profileImage.visibility = View.VISIBLE
            binding.process.visibility=View.VISIBLE
            binding.processlayout.visibility=View.VISIBLE
            binding.profileName.text = it.child("name").value.toString()
            binding.profileEmail.text = it.child("email").value.toString()
            //binding.password.text = it.child("password").value.toString()
        }
    }

    private fun konuSonuDegerlendirme() {
        val user = Firebase.auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val userRef = Firebase.database.reference.child("Users").child(userId)
                .child("QuizResults")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var text = ""

                    for (categorySnapshot in snapshot.children) {
                        val category = categorySnapshot.key // Kategorinin adını al

                        if (category != null) {
                            var correctCount = 0L
                            var wrongCount = 0L

                            for (questionSnapshot in categorySnapshot.children) {
                                val currentIsCorrect =
                                    questionSnapshot.child("currentiscorrect").getValue(Boolean::class.java) ?: false

                                // Sorunun durumuna göre doğru veya yanlış sayısını arttır
                                if (currentIsCorrect) {
                                    correctCount++
                                } else {
                                    wrongCount++
                                }
                                println("Category: $category, QuestionID: ${questionSnapshot.key}, IsCorrect: $currentIsCorrect")
                            }

                            // Her bir kategori için doğru ve yanlış sayıları
                            text += ("\n$category: \nCorrect Count: $correctCount\nWrong Count: $wrongCount\n")

                            // Her bir kategori için doğru ve yanlış sayıları sıfırladık
                            correctCount = 0L
                            wrongCount = 0L
                        }
                    }
                    // TextView'e sonuçları yazdırdım
                    binding.textView3.text = text
                }
                override fun onCancelled(error: DatabaseError) {
                    println("Failed to retrieve quiz results: ${error.message}")
                }
            })
        }
    }

}
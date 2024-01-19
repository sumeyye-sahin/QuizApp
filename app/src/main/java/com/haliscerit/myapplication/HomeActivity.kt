package com.haliscerit.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.adapter.categoryadapter
import com.haliscerit.myapplication.databinding.ActivityHomeBinding
import com.haliscerit.myapplication.model.Categorymodelclass

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding
    private lateinit var categoryList: ArrayList<Categorymodelclass>
    private lateinit var adapter: categoryadapter
    private var toplamsorusayisi: Int = 0
    private var bakilansorusayisi: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        //getTotalQuestionsAttempted()

        var bottomNav = binding.bottomNavigationView
        bottomNav.setSelectedItemId(R.id.home)
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.logout -> {
                    showLogoutConfirmationDialog()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }



        categoryList = ArrayList<Categorymodelclass>()
        categoryList.add(Categorymodelclass(R.drawable.cat_img1, "Today’s Networking",))
        categoryList.add(Categorymodelclass(R.drawable.cat_img5, "Layer 2 Data Link"))
        categoryList.add(Categorymodelclass(R.drawable.cat_img3, "Network Communications And Connectivity"))
        categoryList.add(Categorymodelclass(R.drawable.cat_img4, "Ethernet Concepts"))
        categoryList.add(Categorymodelclass(R.drawable.cat_img10, "Network's Models And Protocols"))
        categoryList.add(Categorymodelclass(R.drawable.cat_img8, "Numbering Systems"))
        categoryList.add(Categorymodelclass(R.drawable.cat_img9, "Switching \"Ethernet Protocol\""))
        binding.apply {
            adapter = categoryadapter(categoryList)
            categoryrecyclerView.adapter = adapter
            categoryrecyclerView.setHasFixedSize(true)
            categoryrecyclerView.layoutManager =
                StaggeredGridLayoutManager(
                    1,
                    RecyclerView.VERTICAL
                ) //StringgeredGridLayoutManager: Sütun sayısı 2, dikey yönde düzenle
        }

// Firestore referansını al
        val firestore = FirebaseFirestore.getInstance()

// Tüm kategorilerin listesi
        val allCategories = listOf(
            "Network Communications And Connectivity",
            "Layer 2 Data Link",
            "Ethernet Concepts",
            "Network's Models And Protocols",
            "Numbering Systems",
            "Switching \"Ethernet Protocol\"",
            "Today’s Networking"
        )

// Toplam soru sayısını saklamak için bir değişken
        var totalQuestionCount = 0

// Tüm kategoriler için asenkron işlemleri başlat
        val tasks: List<Task<QuerySnapshot>> = allCategories.map { category ->
            val questionReference = firestore.collection("Questions")
                .document(category)
                .collection("question1")
                .get()

            questionReference
        }

// Tüm kategorilerin işlemlerinin tamamlanmasını bekleyin
        Tasks.whenAllSuccess<QuerySnapshot>(tasks)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.forEachIndexed { index, snapshot ->
                        val questionCount = snapshot.size()
                        totalQuestionCount += questionCount
                      //  println("'$${allCategories[index]}' kategorisindeki soru sayısı: $questionCount")
                    }

                    // Toplam soru sayısını yazdır
                   // println("Tüm kategorilerdeki toplam soru sayısı: $totalQuestionCount")
                    binding.toplamsorusayisi.text = totalQuestionCount.toString()

                    // Verileri çektikten sonra oranı hesapla ve güncelle
                    val user = Firebase.auth.currentUser
                    val userId = user?.uid

                    if (userId != null) {
                        val userRef = Firebase.database.reference.child("Users").child(userId)
                            .child("QuizResults")

                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var totalQuestionsAttempted = 0

                                for (categorySnapshot in snapshot.children) {
                                    val category = categorySnapshot.key // Kategorinin adını al

                                    if (category != null) {
                                        for (questionSnapshot in categorySnapshot.children) {
                                            val questionId = questionSnapshot.key // Soru ID'sini al

                                            if (questionId != null) {
                                                // Örneğin, soru ID'sini kullanarak soruyu getirme veya işleme yapma
                                                // Burada sadece kaç farklı soruya bakıldığını sayıyoruz
                                                totalQuestionsAttempted++
                                            }
                                        }
                                    }
                                }

                              //  println("Toplam sorulara bakılan sayı: $totalQuestionsAttempted")
                                binding.bakilansorusayisi.text = totalQuestionsAttempted.toString()

                                // Hesaplamayı burada yap ve oranı güncelle
                                if (totalQuestionsAttempted > 0) {
                                    val successRate =
                                        (totalQuestionsAttempted * 100) / totalQuestionCount
                                    binding.oran.text = successRate.toString()
                                } else {
                                    // Eğer hiç soru bakılmamışsa oranı 0 olarak ayarla
                                    binding.oran.text = "0"
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                print("Failed to retrieve total questions attempted: ${error.message}")
                            }
                        })
                    }
                } else {
                    println("Soru sayıları alınırken hata oluştu: ${task.exception}")
                }
            }
    }
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Quit App?")
        builder.setMessage("You are about to quit the application, are you sure?")

        builder.setPositiveButton("Yes") { dialog, which ->
            // Kullanıcı "Evet" dediğinde çıkış işlemini gerçekleştirin
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, which ->
            // Kullanıcı "Hayır" dediğinde dialog'u kapatın
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}
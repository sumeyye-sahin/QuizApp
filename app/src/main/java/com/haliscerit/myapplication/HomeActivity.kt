package com.haliscerit.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.haliscerit.myapplication.adapter.categoryadapter
import com.haliscerit.myapplication.databinding.ActivityHomeBinding
import com.haliscerit.myapplication.model.Categorymodelclass

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding
    private lateinit var categoryList: ArrayList<Categorymodelclass>
    private lateinit var adapter: categoryadapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        var bottomNav= binding.bottomNavigationView
        bottomNav.setSelectedItemId(R.id.home)
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0,0)
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

        categoryList = ArrayList<Categorymodelclass>()
        categoryList.add(Categorymodelclass(R.drawable.images, "Ports"))
        categoryList.add(Categorymodelclass(R.drawable.network, "Kategori 2"))
        categoryList.add(Categorymodelclass(R.drawable.images, "Kategori 1"))
        categoryList.add(Categorymodelclass(R.drawable.images, "Kategori 1"))
        categoryList.add(Categorymodelclass(R.drawable.images, "Kategori 1"))
        categoryList.add(Categorymodelclass(R.drawable.images, "Kategori 1"))
        categoryList.add(Categorymodelclass(R.drawable.images, "Kategori 1"))
        binding.apply {
            adapter = categoryadapter(categoryList)
            categoryrecyclerView.adapter = adapter
            categoryrecyclerView.setHasFixedSize(true)
            categoryrecyclerView.layoutManager =
                StaggeredGridLayoutManager(1, RecyclerView.VERTICAL) //StringgeredGridLayoutManager: Sütun sayısı 2, dikey yönde düzenle
        }
    }
}
package com.haliscerit.myapplication.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.haliscerit.myapplication.R
import com.haliscerit.myapplication.adapter.CategoryAdapter
import com.haliscerit.myapplication.databinding.ActivityHomeBinding
import com.haliscerit.myapplication.model.CategoryModelClass
import com.haliscerit.myapplication.repository.CategoryRepository
import com.haliscerit.myapplication.viewmodel.HomeViewModel
import com.haliscerit.myapplication.viewmodel.ViewModelFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: CategoryAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val categoryRepository = CategoryRepository()
        viewModel = ViewModelProvider(this, ViewModelFactory(categoryRepository = categoryRepository)).get(HomeViewModel::class.java)

        setupObservers()
        setupBottomNavigation()
        setupRecyclerView()

        viewModel.loadQuestionCounts()
        auth.currentUser?.uid?.let { viewModel.loadUserQuizResults(it) }
    }

    private fun setupObservers() {
        viewModel.totalQuestionCount.observe(this) {
            binding.toplamsorusayisi.text = it.toString()
        }
        viewModel.totalQuestionsAttempted.observe(this) {
            binding.bakilansorusayisi.text = it.toString()
        }
        viewModel.successRate.observe(this) {
            binding.oran.text = it.toString()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> startActivity(Intent(this, ProfileActivity::class.java))
                R.id.logout -> showLogoutConfirmationDialog()
            }
            true
        }
    }

    private fun setupRecyclerView() {
        val categoryList = arrayListOf(
            CategoryModelClass(R.drawable.cat_img1, "Today’s Networking"),
            CategoryModelClass(R.drawable.cat_img5, "Layer 2 Data Link"),
            CategoryModelClass(R.drawable.cat_img3, "Network Communications And Connectivity"),
            CategoryModelClass(R.drawable.cat_img4, "Ethernet Concepts"),
            CategoryModelClass(R.drawable.cat_img10, "Network's Models And Protocols"),
            CategoryModelClass(R.drawable.cat_img8, "Numbering Systems"),
            CategoryModelClass(R.drawable.cat_img9, "Switching \"Ethernet Protocol\"")
        )
        adapter = CategoryAdapter(categoryList)
        binding.categoryrecyclerView.layoutManager = StaggeredGridLayoutManager(1, RecyclerView.VERTICAL)
        binding.categoryrecyclerView.adapter = adapter
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Quit App?")
            setMessage("You are about to quit the application, are you sure?")
            setPositiveButton("Yes") { dialog, _ ->
                auth.signOut()
                startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }
}

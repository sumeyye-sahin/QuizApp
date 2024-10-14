package com.haliscerit.myapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haliscerit.myapplication.databinding.CategoryitemBinding
import com.haliscerit.myapplication.model.CategoryModelClass
import com.haliscerit.myapplication.view.QuizActivity

class CategoryAdapter(private val categoryList: List<CategoryModelClass>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: CategoryitemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryitemBinding.inflate(inflater, parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]

        holder.binding.imageView.setImageResource(category.image)
        holder.binding.textView.text = category.text

        holder.binding.touchableArea.setOnClickListener {
            val context = holder.binding.root.context
            val intent = Intent(context, QuizActivity::class.java)
            intent.putExtra("questionType", category.text)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}

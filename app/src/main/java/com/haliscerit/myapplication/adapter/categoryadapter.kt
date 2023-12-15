package com.haliscerit.myapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haliscerit.myapplication.QuizActivity
import com.haliscerit.myapplication.databinding.CategoryitemBinding
import com.haliscerit.myapplication.model.Categorymodelclass


class categoryadapter(var categoryList: ArrayList<Categorymodelclass>) : RecyclerView.Adapter<categoryadapter.MycategoryViewHolder>(){
    class MycategoryViewHolder(var itemBinding: CategoryitemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MycategoryViewHolder {

        return MycategoryViewHolder(CategoryitemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: MycategoryViewHolder, position: Int) {

        var dataList = categoryList[position]
        holder.itemBinding.imageView.setImageResource(dataList.image)
        holder.itemBinding.textView.text = dataList.text
        holder.itemBinding.touchableArea.setOnClickListener {
            var intent = Intent(
            holder.itemBinding.touchableArea.context,
            QuizActivity::class.java
            )
            intent.putExtra("imageView3",dataList.image)
            intent.putExtra("questionType",dataList.text)
            //start
            holder.itemBinding.touchableArea.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
package com.example.notes.adapter

import android.animation.ObjectAnimator
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.Util
import com.example.notes.R
import com.example.notes.model.NoteData
import com.example.notes.utils.Utils

class ImageAdapter(private val listener: OnImageClick) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    private var imageList: ArrayList<String> = arrayListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgImage)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setAnimation(holder.itemView)
        val imagePath = imageList[position]
        Glide.with(holder.itemView.context).load(imagePath)
            .error(R.drawable.error)
            .into(holder.image)
        holder.btnDelete.setOnClickListener {
            listener.onClick(position)
        }

    }

    override fun getItemCount(): Int = imageList.size

    fun updateImageData(imagePaths: List<String>) {
        imageList.clear()
        imageList.addAll(imagePaths)
        notifyDataSetChanged()
    }

    fun removeImageData(position: Int) {
        if (position >= 0 && position < imageList.size) {
            imageList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, imageList.size)
        }
    }
    private fun setAnimation(view: View) {
        val animation = AnimationUtils.loadAnimation(
            view.context,
            android.R.anim.fade_in
        )
        animation.repeatCount = 0  // Ensure the animation doesn't repeat indefinitely
        view.startAnimation(animation)
    }
}

interface OnImageClick {
    fun onClick(position: Int)
}
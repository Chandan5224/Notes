package com.example.notes.adapter

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.util.Util
import com.example.notes.R
import com.example.notes.model.NoteData
import com.example.notes.utils.Utils

class ImageAdapter(private val listener: OnNoteClick) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<NoteData>() {
        override fun areItemsTheSame(
            oldItem: NoteData, newItem: NoteData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NoteData, newItem: NoteData
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.etTitle)
        val body: TextView = view.findViewById(R.id.etBody)
        val container: CardView = view.findViewById(R.id.cardviewNoteItem)
        val time: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val note = differ.currentList[position]

    }

    override fun getItemCount(): Int = differ.currentList.size


}

interface OnImageClick {
    fun onClick(noteData: NoteData)
}
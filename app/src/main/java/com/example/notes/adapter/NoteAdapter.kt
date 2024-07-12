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

class NoteAdapter(private val listener: OnNoteClick) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private var lastColor: Int? = null

    // Define a list of colors
    private val colors = listOf(
        Color.RED,
        Color.GREEN,
        Color.YELLOW,
        Color.CYAN,
        Color.MAGENTA
    )

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
        if (note.title.isBlank())
            holder.title.text = "Note title"
        else
            holder.title.text = note.title

        holder.body.text = note.body
        holder.time.text = Utils.formatDateTimeToDisplay(note.timestamp)
        // Generate a random color and ensure it's not the same as the last one
        val randomColor = generateUniqueColor(lastColor)
        holder.container.setCardBackgroundColor(randomColor)
        lastColor = randomColor

        holder.itemView.setOnClickListener {
            listener.onClick(note)
        }
        holder.itemView.setOnLongClickListener {
            listener.onLongClick(note)
            true
        }

        setAnimation(holder.title)
        setAnimation(holder.body)

    }

    override fun getItemCount(): Int = differ.currentList.size

    private fun generateUniqueColor(lastColor: Int?): Int {
        var newColor: Int
        do {
            newColor = colors.random()
        } while (newColor == lastColor)
        return newColor
    }

    private fun setAnimation(view: View) {
        val animation = android.view.animation.AnimationUtils.loadAnimation(
            view.context,
            android.R.anim.slide_in_left
        )
        animation.repeatCount = 0  // Ensure the animation doesn't repeat indefinitely
        view.startAnimation(animation)
    }
}

interface OnNoteClick {
    fun onClick(noteData: NoteData)
    fun onLongClick(noteData: NoteData)
}
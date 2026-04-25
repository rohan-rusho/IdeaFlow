package com.flowideafmaker.ideaflow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flowideafmaker.ideaflow.databinding.ItemIdeaListCardBinding

class IdeaAdapter(
    private var ideas: List<Idea>,
    private val onIdeaClick: (Idea) -> Unit
) : RecyclerView.Adapter<IdeaAdapter.IdeaViewHolder>() {

    fun updateData(newIdeas: List<Idea>) {
        ideas = newIdeas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaViewHolder {
        val binding = ItemIdeaListCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IdeaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IdeaViewHolder, position: Int) {
        holder.bind(ideas[position])
    }

    override fun getItemCount(): Int = ideas.size

    inner class IdeaViewHolder(private val binding: ItemIdeaListCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(idea: Idea) {
            binding.tvTitle.text = idea.title
            binding.tvCategory.text = idea.category
            binding.tvPriority.text = "${idea.priority} Priority"
            binding.tvTime.text = idea.timeAgo
            binding.tvStatus.text = idea.status
            
            // Set status color based on status
            when (idea.status.lowercase()) {
                "completed" -> {
                    binding.cardStatus.setCardBackgroundColor(android.graphics.Color.parseColor("#DCFCE7"))
                    binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#166534"))
                }
                "developing" -> {
                    binding.cardStatus.setCardBackgroundColor(android.graphics.Color.parseColor("#FEF9C3"))
                    binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#854D0E"))
                }
                else -> {
                    binding.cardStatus.setCardBackgroundColor(android.graphics.Color.parseColor("#F3F4F6"))
                    binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#374151"))
                }
            }
            
            binding.root.setOnClickListener { onIdeaClick(idea) }
        }
    }
}
package com.flowideafmaker.ideaflow

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.flowideafmaker.ideaflow.databinding.ItemFlowCardBinding

class FlowAdapter(
    private var ideas: List<Idea>,
    private val onIdeaClick: (Idea) -> Unit,
    private val onStepClick: (Idea, Int) -> Unit
) : RecyclerView.Adapter<FlowAdapter.FlowViewHolder>() {

    fun updateData(newIdeas: List<Idea>) {
        ideas = newIdeas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowViewHolder {
        val binding = ItemFlowCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FlowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlowViewHolder, position: Int) {
        holder.bind(ideas[position])
    }

    override fun getItemCount(): Int = ideas.size

    inner class FlowViewHolder(private val binding: ItemFlowCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(idea: Idea) {
            binding.tvTitle.text = idea.title
            binding.tvPercent.text = "${idea.progress}%"
            binding.tvStage.text = "● ${getStageName(idea.progress)}"
            binding.progressBar.setProgress(idea.progress, true)

            updateSteps(idea)

            binding.root.setOnClickListener { onIdeaClick(idea) }
            
            binding.flowSteps.stepIdeation.setOnClickListener { onStepClick(idea, 10) }
            binding.flowSteps.stepResearch.setOnClickListener { onStepClick(idea, 30) }
            binding.flowSteps.stepPrototype.setOnClickListener { onStepClick(idea, 70) }
            binding.flowSteps.stepLaunch.setOnClickListener { onStepClick(idea, 100) }
        }

        private fun getStageName(progress: Int): String {
            return when {
                progress >= 100 -> "EXECUTION STAGE"
                progress >= 70 -> "PLANNING STAGE"
                progress >= 30 -> "RESEARCH STAGE"
                else -> "CONCEPT STAGE"
            }
        }

        private fun updateSteps(idea: Idea) {
            val context = binding.root.context
            val activeColor = ContextCompat.getColor(context, R.color.primary_indigo)
            val inactiveColor = ContextCompat.getColor(context, R.color.text_hint)
            val activeBg = R.drawable.bg_step_completed
            val inactiveBg = R.drawable.bg_step_inactive

            val progress = idea.progress

            // Step 1: Concept
            updateStepUI(binding.flowSteps.ivStep1, binding.flowSteps.tvStep1, progress >= 0, activeColor, inactiveColor, activeBg, inactiveBg)
            
            // Step 2: Research
            val step2Active = progress >= 30
            updateStepUI(binding.flowSteps.ivStep2, binding.flowSteps.tvStep2, step2Active, activeColor, inactiveColor, activeBg, inactiveBg)
            binding.flowSteps.line1.setBackgroundColor(if (step2Active) activeColor else ContextCompat.getColor(context, R.color.card_stroke))

            // Step 3: Planning
            val step3Active = progress >= 70
            updateStepUI(binding.flowSteps.ivStep3, binding.flowSteps.tvStep3, step3Active, activeColor, inactiveColor, activeBg, inactiveBg)
            binding.flowSteps.line2.setBackgroundColor(if (step3Active) activeColor else ContextCompat.getColor(context, R.color.card_stroke))

            // Step 4: Execution
            val step4Active = progress >= 100
            updateStepUI(binding.flowSteps.ivStep4, binding.flowSteps.tvStep4, step4Active, activeColor, inactiveColor, activeBg, inactiveBg)
            binding.flowSteps.line3.setBackgroundColor(if (step4Active) activeColor else ContextCompat.getColor(context, R.color.card_stroke))
        }

        private fun updateStepUI(imageView: View, textView: android.widget.TextView, isActive: Boolean, activeColor: Int, inactiveColor: Int, activeBg: Int, inactiveBg: Int) {
            val context = binding.root.context
            imageView.setBackgroundResource(if (isActive) activeBg else inactiveBg)
            if (imageView is android.widget.ImageView) {
                imageView.imageTintList = ColorStateList.valueOf(if (isActive) ContextCompat.getColor(context, R.color.white) else inactiveColor)
            }
            textView.setTextColor(if (isActive) activeColor else inactiveColor)
        }
    }
}
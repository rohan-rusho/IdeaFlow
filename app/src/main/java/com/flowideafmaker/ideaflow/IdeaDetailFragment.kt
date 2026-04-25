package com.flowideafmaker.ideaflow

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.flowideafmaker.ideaflow.databinding.FragmentIdeaDetailBinding

class IdeaDetailFragment : Fragment() {

    private var _binding: FragmentIdeaDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IdeaViewModel by activityViewModels()
    
    private var idea: Idea? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idea = it.getParcelable(ARG_IDEA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIdeaDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        idea?.let { initialIdea ->
            binding.tvTitle.text = initialIdea.title
            binding.tvDescription.text = initialIdea.description
            
            // Observe changes for this specific idea
            viewModel.ideas.observe(viewLifecycleOwner) { ideas ->
                ideas.find { it.id == initialIdea.id }?.let { updatedIdea ->
                    updateUI(updatedIdea)
                }
            }

            binding.btnPlus.setOnClickListener {
                val currentProgress = viewModel.ideas.value?.find { it.id == initialIdea.id }?.progress ?: 0
                viewModel.updateProgress(initialIdea.id, currentProgress + 10)
            }

            binding.btnMinus.setOnClickListener {
                val currentProgress = viewModel.ideas.value?.find { it.id == initialIdea.id }?.progress ?: 0
                viewModel.updateProgress(initialIdea.id, currentProgress - 10)
            }

            binding.btnMarkComplete.setOnClickListener {
                viewModel.updateProgress(initialIdea.id, 100)
            }

            binding.flowSteps.stepIdeation.setOnClickListener { viewModel.updateProgress(initialIdea.id, 10) }
            binding.flowSteps.stepResearch.setOnClickListener { viewModel.updateProgress(initialIdea.id, 30) }
            binding.flowSteps.stepPrototype.setOnClickListener { viewModel.updateProgress(initialIdea.id, 70) }
            binding.flowSteps.stepLaunch.setOnClickListener { viewModel.updateProgress(initialIdea.id, 100) }

            binding.btnEdit.setOnClickListener {
                val editFragment = AddEditIdeaFragment.newInstance(initialIdea)
                parentFragmentManager.beginTransaction()
                    .replace(android.R.id.content, editFragment)
                    .addToBackStack(null)
                    .commit()
            }

            binding.btnDelete.setOnClickListener {
                showDeleteConfirmation(initialIdea.id)
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showDeleteConfirmation(ideaId: String) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Idea")
            .setMessage("Are you sure you want to delete this idea? This action cannot be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteIdea(ideaId)
                parentFragmentManager.popBackStack()
            }
            .show()
    }

    private fun updateUI(idea: Idea) {
        binding.tvCategory.text = idea.category
        val progress = idea.progress
        binding.progressBar.setProgress(progress, true)
        binding.tvProgressPercent.text = "$progress% Complete"

        val activeColor = ContextCompat.getColor(requireContext(), R.color.primary_indigo)
        val inactiveColor = ContextCompat.getColor(requireContext(), R.color.text_hint)
        val activeBg = R.drawable.bg_step_completed
        val inactiveBg = R.drawable.bg_step_inactive
        val lineColorActive = activeColor
        val lineColorInactive = ContextCompat.getColor(requireContext(), R.color.card_stroke)

        // Step 1: Ideation (0%+)
        updateStepUI(binding.flowSteps.ivStep1, binding.flowSteps.tvStep1, progress >= 0, activeColor, inactiveColor, activeBg, inactiveBg)

        // Step 2: Research (30%+)
        val isStep2Active = progress >= 30
        updateStepUI(binding.flowSteps.ivStep2, binding.flowSteps.tvStep2, isStep2Active, activeColor, inactiveColor, activeBg, inactiveBg)
        binding.flowSteps.line1.setBackgroundColor(if (isStep2Active) lineColorActive else lineColorInactive)

        // Step 3: Planning (70%+)
        val isStep3Active = progress >= 70
        updateStepUI(binding.flowSteps.ivStep3, binding.flowSteps.tvStep3, isStep3Active, activeColor, inactiveColor, activeBg, inactiveBg)
        binding.flowSteps.line2.setBackgroundColor(if (isStep3Active) lineColorActive else lineColorInactive)

        // Step 4: Launch (100%)
        val isStep4Active = progress >= 100
        updateStepUI(binding.flowSteps.ivStep4, binding.flowSteps.tvStep4, isStep4Active, activeColor, inactiveColor, activeBg, inactiveBg)
        binding.flowSteps.line3.setBackgroundColor(if (isStep4Active) lineColorActive else lineColorInactive)
    }

    private fun updateStepUI(imageView: View, textView: android.widget.TextView, isActive: Boolean, activeColor: Int, inactiveColor: Int, activeBg: Int, inactiveBg: Int) {
        imageView.setBackgroundResource(if (isActive) activeBg else inactiveBg)
        if (imageView is android.widget.ImageView) {
            imageView.imageTintList = ColorStateList.valueOf(if (isActive) ContextCompat.getColor(requireContext(), R.color.white) else inactiveColor)
        }
        textView.setTextColor(if (isActive) activeColor else inactiveColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IDEA = "arg_idea"

        fun newInstance(idea: Idea): IdeaDetailFragment {
            val fragment = IdeaDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_IDEA, idea)
            fragment.arguments = args
            return fragment
        }
    }
}
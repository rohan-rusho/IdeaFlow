package com.flowideafmaker.ideaflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.flowideafmaker.ideaflow.databinding.FragmentAddEditIdeaBinding
import java.util.UUID

class AddEditIdeaFragment : Fragment() {

    private var _binding: FragmentAddEditIdeaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IdeaViewModel by activityViewModels()

    private var existingIdea: Idea? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            existingIdea = it.getParcelable(ARG_IDEA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditIdeaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupCategorySpinner()

        existingIdea?.let { idea ->
            binding.tvHeader.text = "Edit Idea"
            binding.etTitle.setText(idea.title)
            binding.etDescription.setText(idea.description)
            
            val categories = listOf("Tech", "Business", "Personal")
            val catIndex = categories.indexOfFirst { it.equals(idea.category, ignoreCase = true) }
            if (catIndex != -1) binding.spinnerCategory.setSelection(catIndex)

            when (idea.priority) {
                "High" -> binding.togglePriority.check(R.id.btnHigh)
                "Medium" -> binding.togglePriority.check(R.id.btnMedium)
                else -> binding.togglePriority.check(R.id.btnLow)
            }
        } ?: run {
            binding.tvHeader.text = "Add Idea"
            binding.togglePriority.check(R.id.btnLow)
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnSave.setOnClickListener {
            saveIdea()
        }
    }

    private fun setupCategorySpinner() {
        val categories = listOf("Tech", "Business", "Personal")
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun saveIdea() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem?.toString() ?: "TECH"
        
        val priority = when (binding.togglePriority.checkedButtonId) {
            R.id.btnHigh -> "High"
            R.id.btnMedium -> "Medium"
            else -> "Low"
        }

        if (title.isEmpty()) {
            binding.etTitle.error = "Title required"
            return
        }

        if (existingIdea != null) {
            val updatedIdea = existingIdea!!.copy(
                title = title,
                description = description,
                category = category.uppercase(),
                priority = priority
            )
            viewModel.updateIdea(updatedIdea)
        } else {
            val newIdea = Idea(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                category = category.uppercase(),
                status = "Draft",
                priority = priority,
                progress = 0,
                timeAgo = "Just now"
            )
            viewModel.addIdea(newIdea)
        }
        
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IDEA = "arg_idea"

        fun newInstance(idea: Idea? = null): AddEditIdeaFragment {
            val fragment = AddEditIdeaFragment()
            if (idea != null) {
                val args = Bundle()
                args.putParcelable(ARG_IDEA, idea)
                fragment.arguments = args
            }
            return fragment
        }
    }
}
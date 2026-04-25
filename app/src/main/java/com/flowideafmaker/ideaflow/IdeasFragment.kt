package com.flowideafmaker.ideaflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.flowideafmaker.ideaflow.databinding.FragmentIdeasBinding

class IdeasFragment : Fragment() {

    private var _binding: FragmentIdeasBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IdeaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIdeasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = IdeaAdapter(emptyList()) { idea ->
            openDetail(idea)
        }
        binding.rvIdeas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIdeas.adapter = adapter

        viewModel.filteredIdeas.observe(viewLifecycleOwner) { ideas ->
            adapter.updateData(ideas)
        }

        viewModel.currentCategory.observe(viewLifecycleOwner) { category ->
            updateFilterButtons(category)
        }

        binding.btnFilterAll.setOnClickListener { viewModel.setCategory("All") }
        binding.btnFilterTech.setOnClickListener { viewModel.setCategory("Tech") }
        binding.btnFilterBusiness.setOnClickListener { viewModel.setCategory("Business") }
        binding.btnFilterPersonal.setOnClickListener { viewModel.setCategory("Personal") }

        binding.fabAddIdea.setOnClickListener {
            val addFragment = AddEditIdeaFragment()
            parentFragmentManager.beginTransaction()
                .replace(android.R.id.content, addFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.btnHistory.setOnClickListener {
            val historyFragment = HistoryFragment()
            parentFragmentManager.beginTransaction()
                .replace(android.R.id.content, historyFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        updateMenuIcon()
        parentFragmentManager.addOnBackStackChangedListener {
            updateMenuIcon()
        }
    }

    private fun updateMenuIcon() {
        if (_binding == null) return
        val hasBackStack = parentFragmentManager.backStackEntryCount > 0
        if (hasBackStack) {
            binding.btnMenu.setImageResource(R.drawable.ic_back)
        } else {
            binding.btnMenu.setImageResource(R.drawable.ic_menu)
        }
    }

    private fun updateFilterButtons(selectedCategory: String) {
        val buttons = listOf(
            binding.btnFilterAll to "All",
            binding.btnFilterTech to "Tech",
            binding.btnFilterBusiness to "Business",
            binding.btnFilterPersonal to "Personal"
        )

        buttons.forEach { (button, category) ->
            val isSelected = category.equals(selectedCategory, ignoreCase = true)
            button.backgroundTintList = android.content.res.ColorStateList.valueOf(
                if (isSelected) androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_indigo)
                else androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white)
            )
            button.setTextColor(
                if (isSelected) androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white)
                else androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary)
            )
        }
    }

    private fun openDetail(idea: Idea) {
        val detailFragment = IdeaDetailFragment.newInstance(idea)
        parentFragmentManager.beginTransaction()
            .replace(android.R.id.content, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
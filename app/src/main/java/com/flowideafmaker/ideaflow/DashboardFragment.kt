package com.flowideafmaker.ideaflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flowideafmaker.ideaflow.databinding.FragmentDashboardBinding

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IdeaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = IdeaAdapter(emptyList()) { idea ->
            openDetail(idea)
        }
        binding.rvRecentIdeas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentIdeas.adapter = adapter

        viewModel.ideas.observe(viewLifecycleOwner) { ideas ->
            adapter.updateData(ideas.take(10)) // Show up to 10 most recent ideas
            
            // Update summary statistics
            binding.tvTotalIdeas.text = ideas.size.toString()
            binding.tvActiveFlows.text = ideas.count { it.status == "Developing" }.toString()
        }

        binding.fabAddIdea.setOnClickListener {
            val addFragment = AddEditIdeaFragment()
            parentFragmentManager.beginTransaction()
                .replace(android.R.id.content, addFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnSearch.setOnClickListener {
            (activity as? MainActivity)?.let { mainActivity ->
                mainActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation).selectedItemId = R.id.ideasFragment
            }
        }

        binding.btnHistory.setOnClickListener {
            val historyFragment = HistoryFragment()
            parentFragmentManager.beginTransaction()
                .replace(android.R.id.content, historyFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.tvViewAll.setOnClickListener {
            (activity as? MainActivity)?.let { mainActivity ->
                mainActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation).selectedItemId = R.id.ideasFragment
            }
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
package com.flowideafmaker.ideaflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.flowideafmaker.ideaflow.databinding.FragmentFlowBinding

class FlowFragment : Fragment() {

    private var _binding: FragmentFlowBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IdeaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = FlowAdapter(emptyList(), 
            onIdeaClick = { idea -> openDetail(idea) },
            onStepClick = { idea, newProgress ->
                viewModel.updateProgress(idea.id, newProgress)
            }
        )
        binding.rvFlow.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFlow.adapter = adapter

        viewModel.ideas.observe(viewLifecycleOwner) { ideas ->
            adapter.updateData(ideas.filter { it.status == "Developing" })
        }

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
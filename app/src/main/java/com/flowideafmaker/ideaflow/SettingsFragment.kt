package com.flowideafmaker.ideaflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flowideafmaker.ideaflow.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnReset.setOnClickListener {
            val bottomSheet = ResetBottomSheet()
            bottomSheet.show(childFragmentManager, "ResetBottomSheet")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
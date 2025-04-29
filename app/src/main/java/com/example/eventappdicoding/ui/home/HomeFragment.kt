package com.example.eventappdicoding.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventappdicoding.R
import com.example.eventappdicoding.databinding.FragmentHomeBinding
import com.example.eventappdicoding.ui.adapter.EventListAdapter // For finished list
import com.example.eventappdicoding.ui.adapter.EventListHorizontalAdapter // For active list


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var activeEventAdapter: EventListHorizontalAdapter // Use horizontal adapter
    private lateinit var finishedEventAdapter: EventListAdapter       // Use standard adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()

        // ViewModel fetches data in init, or you can add a refresh mechanism here
        // homeViewModel.fetchHomeEvents() // Can be called if refresh is needed
    }

    private fun setupRecyclerViews() {
        // Active Events (Horizontal)
        activeEventAdapter = EventListHorizontalAdapter { event ->
            // Navigate to detail using a global action if defined, or specific if needed
            // Make sure HomeFragment has an action defined in mobile_navigation.xml
            val action = HomeFragmentDirections.actionHomeFragmentToEventDetail(event.id)
            findNavController().navigate(action)
        }
        binding.rvHomeActiveEvents.apply {
            adapter = activeEventAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        // Finished Events (Vertical)
        finishedEventAdapter = EventListAdapter { event ->
            val action = HomeFragmentDirections.actionHomeFragmentToEventDetail(event.id)
            findNavController().navigate(action)
        }
        binding.rvHomeFinishedEvents.apply {
            adapter = finishedEventAdapter
            // LayoutManager is set in XML, but good practice to set it here too
            layoutManager = LinearLayoutManager(requireContext())
            // Disable nested scrolling for the vertical RV inside NestedScrollView
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        // Observe Active Events
        homeViewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            activeEventAdapter.submitList(events)
            // Adjust visibility based on loading/error state as well
            binding.rvHomeActiveEvents.isVisible = !events.isNullOrEmpty()
            binding.tvErrorHomeActive.isVisible = events.isNullOrEmpty() && !homeViewModel.isLoadingActive.value!! && homeViewModel.errorActive.value == null
            if (binding.tvErrorHomeActive.isVisible) {
                binding.tvErrorHomeActive.text = getString(R.string.no_active_events_home) // Use specific string
            }
            updateFinishedEventsLabelPosition() // Adjust label position
        }
        homeViewModel.isLoadingActive.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarHomeActive.isVisible = isLoading
            // Hide RV and potentially error text while loading
            if (isLoading) {
                binding.rvHomeActiveEvents.isVisible = false
                binding.tvErrorHomeActive.isVisible = false
            }
            updateFinishedEventsLabelPosition()
        }
        homeViewModel.errorActive.observe(viewLifecycleOwner) { error ->
            val showError = error != null && !homeViewModel.isLoadingActive.value!!
            binding.tvErrorHomeActive.isVisible = showError
            binding.rvHomeActiveEvents.isVisible = error == null && !homeViewModel.isLoadingActive.value!!
            if (showError) {
                binding.tvErrorHomeActive.text = error ?: getString(R.string.error_loading_data)
            }
            updateFinishedEventsLabelPosition()
        }


        // Observe Finished Events
        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            finishedEventAdapter.submitList(events)
            binding.rvHomeFinishedEvents.isVisible = !events.isNullOrEmpty()
            binding.tvErrorHomeFinished.isVisible = events.isNullOrEmpty() && !homeViewModel.isLoadingFinished.value!! && homeViewModel.errorFinished.value == null
            if (binding.tvErrorHomeFinished.isVisible) {
                binding.tvErrorHomeFinished.text = getString(R.string.no_finished_events_home) // Use specific string
            }
        }
        homeViewModel.isLoadingFinished.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarHomeFinished.isVisible = isLoading
            if (isLoading) {
                binding.rvHomeFinishedEvents.isVisible = false
                binding.tvErrorHomeFinished.isVisible = false
            }
        }
        homeViewModel.errorFinished.observe(viewLifecycleOwner) { error ->
            val showError = error != null && !homeViewModel.isLoadingFinished.value!!
            binding.tvErrorHomeFinished.isVisible = showError
            binding.rvHomeFinishedEvents.isVisible = error == null && !homeViewModel.isLoadingFinished.value!!
            if (showError) {
                binding.tvErrorHomeFinished.text = error ?: getString(R.string.error_loading_data)
            }
        }
    }

    // Helper to adjust the top constraint of the "Finished Events" label
    private fun updateFinishedEventsLabelPosition() {
        binding.tvLabelFinishedEvents.apply {
            val params = layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            when {
                binding.rvHomeActiveEvents.isVisible -> params.topToBottom = binding.rvHomeActiveEvents.id
                binding.progressBarHomeActive.isVisible -> params.topToBottom = binding.progressBarHomeActive.id
                binding.tvErrorHomeActive.isVisible -> params.topToBottom = binding.tvErrorHomeActive.id
                else -> params.topToBottom = binding.tvLabelActiveEvents.id // Fallback if everything above is gone
            }
            layoutParams = params
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Important: Null out the binding object in Fragments
        binding.rvHomeActiveEvents.adapter = null
        binding.rvHomeFinishedEvents.adapter = null
        _binding = null
    }
}
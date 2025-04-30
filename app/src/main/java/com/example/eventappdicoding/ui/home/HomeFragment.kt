package com.example.eventappdicoding.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Use this import
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventappdicoding.R
import com.example.eventappdicoding.data.Resource // Import Resource
import com.example.eventappdicoding.databinding.FragmentHomeBinding
import com.example.eventappdicoding.di.ViewModelFactory // Import Factory
import com.example.eventappdicoding.ui.adapter.EventListAdapter
import com.example.eventappdicoding.ui.adapter.EventListHorizontalAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Instantiate ViewModel using factory
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity().application) // Pass application context
    }
    private lateinit var activeEventAdapter: EventListHorizontalAdapter
    private lateinit var finishedEventAdapter: EventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()
        // ViewModel fetches data in init
    }

    private fun setupRecyclerViews() {
        activeEventAdapter = EventListHorizontalAdapter { event ->
            val action = HomeFragmentDirections.actionHomeFragmentToEventDetail(event.id)
            findNavController().navigate(action)
        }
        binding.rvHomeActiveEvents.apply {
            adapter = activeEventAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        finishedEventAdapter = EventListAdapter { event ->
            val action = HomeFragmentDirections.actionHomeFragmentToEventDetail(event.id)
            findNavController().navigate(action)
        }
        binding.rvHomeFinishedEvents.apply {
            adapter = finishedEventAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        // Observe Active Events Resource
        homeViewModel.activeEvents.observe(viewLifecycleOwner) { resource ->
            binding.progressBarHomeActive.isVisible = resource is Resource.Loading
            binding.rvHomeActiveEvents.isVisible = resource is Resource.Success && !resource.data.isNullOrEmpty()
            binding.tvErrorHomeActive.isVisible = resource is Resource.Error || (resource is Resource.Success && resource.data.isNullOrEmpty())

            when (resource) {
                is Resource.Loading -> {
                    // Optional: Show shimmer or placeholder
                }
                is Resource.Success -> {
                    if (resource.data.isNullOrEmpty()) {
                        binding.tvErrorHomeActive.text = getString(R.string.no_active_events_home)
                    } else {
                        activeEventAdapter.submitList(resource.data)
                    }
                    updateFinishedEventsLabelPosition()
                }
                is Resource.Error -> {
                    binding.tvErrorHomeActive.text = resource.message ?: getString(R.string.error_loading_data)
                    updateFinishedEventsLabelPosition()
                }
            }
            // Always update position unless loading finished events
            if (homeViewModel.finishedEvents.value !is Resource.Loading) {
                updateFinishedEventsLabelPosition()
            }
        }

        // Observe Finished Events Resource
        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { resource ->
            binding.progressBarHomeFinished.isVisible = resource is Resource.Loading
            binding.rvHomeFinishedEvents.isVisible = resource is Resource.Success && !resource.data.isNullOrEmpty()
            binding.tvErrorHomeFinished.isVisible = resource is Resource.Error || (resource is Resource.Success && resource.data.isNullOrEmpty())

            when (resource) {
                is Resource.Loading -> {
                    // Optional: Show shimmer or placeholder
                }
                is Resource.Success -> {
                    if (resource.data.isNullOrEmpty()) {
                        binding.tvErrorHomeFinished.text = getString(R.string.no_finished_events_home)
                    } else {
                        finishedEventAdapter.submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    binding.tvErrorHomeFinished.text = resource.message ?: getString(R.string.error_loading_data)
                }
            }
            // Make sure finished label position updates after active state is known
            updateFinishedEventsLabelPosition()
        }
    }

    // Helper to adjust the top constraint of the "Finished Events" label
    private fun updateFinishedEventsLabelPosition() {
        // Ensure binding is not null
        if (_binding == null) return

        binding.tvLabelFinishedEvents.apply {
            val params = layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            // Check visibility based on Resource state
            val activeResource = homeViewModel.activeEvents.value
            val activeIsLoading = activeResource is Resource.Loading
            val activeIsSuccessNotEmpty = activeResource is Resource.Success && !activeResource.data.isNullOrEmpty()
            val activeIsErrorOrEmpty = activeResource is Resource.Error || (activeResource is Resource.Success && activeResource.data.isNullOrEmpty())

            when {
                activeIsSuccessNotEmpty -> params.topToBottom = binding.rvHomeActiveEvents.id
                activeIsLoading -> params.topToBottom = binding.progressBarHomeActive.id
                activeIsErrorOrEmpty -> params.topToBottom = binding.tvErrorHomeActive.id
                else -> params.topToBottom = binding.tvLabelActiveEvents.id // Fallback if state is unknown initially
            }
            layoutParams = params
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvHomeActiveEvents.adapter = null
        binding.rvHomeFinishedEvents.adapter = null
        _binding = null
    }
}
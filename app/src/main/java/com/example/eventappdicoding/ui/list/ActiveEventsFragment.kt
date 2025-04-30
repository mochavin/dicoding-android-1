package com.example.eventappdicoding.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Use this import
import androidx.navigation.fragment.findNavController
import com.example.eventappdicoding.R
import com.example.eventappdicoding.data.Resource // Import Resource
import com.example.eventappdicoding.databinding.FragmentActiveEventsBinding
import com.example.eventappdicoding.di.ViewModelFactory // Import Factory
import com.example.eventappdicoding.ui.adapter.EventListAdapter
import com.example.eventappdicoding.ui.viewmodel.EventsViewModel // Import correct ViewModel

class ActiveEventsFragment : Fragment() {

    private var _binding: FragmentActiveEventsBinding? = null
    private val binding get() = _binding!!

    // Instantiate ViewModel using factory
    private val eventsViewModel: EventsViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity().application)
    }
    private lateinit var eventAdapter: EventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        // Trigger fetch if needed (ViewModel logic handles re-fetching)
        eventsViewModel.fetchActiveEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventListAdapter { event ->
            val action = ActiveEventsFragmentDirections.actionActiveEventsToEventDetail(event.id)
            findNavController().navigate(action)
        }
        binding.rvActiveEvents.adapter = eventAdapter
    }

    private fun observeViewModel() {
        eventsViewModel.activeEvents.observe(viewLifecycleOwner) { resource ->
            binding.progressBarActive.isVisible = resource is Resource.Loading
            binding.rvActiveEvents.isVisible = resource is Resource.Success && !resource.data.isNullOrEmpty()
            binding.tvErrorActive.isVisible = resource is Resource.Error || (resource is Resource.Success && resource.data.isNullOrEmpty())

            when (resource) {
                is Resource.Loading -> {
                    // Handle loading state (progress bar is already visible)
                }
                is Resource.Success -> {
                    if (resource.data.isNullOrEmpty()) {
                        // Show "No events" message
                        binding.tvErrorActive.text = getString(R.string.no_active_events)
                    } else {
                        // Update the adapter
                        eventAdapter.submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    // Show error message
                    binding.tvErrorActive.text = resource.message ?: getString(R.string.error_loading_data)
                    // Optionally, clear the adapter list on persistent errors
                    // eventAdapter.submitList(emptyList())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvActiveEvents.adapter = null
        _binding = null
    }
}
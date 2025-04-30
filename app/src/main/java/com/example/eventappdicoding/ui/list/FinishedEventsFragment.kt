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
import com.example.eventappdicoding.databinding.FragmentFinishedEventsBinding
import com.example.eventappdicoding.di.ViewModelFactory // Import Factory
import com.example.eventappdicoding.ui.adapter.EventListAdapter
import com.example.eventappdicoding.ui.viewmodel.EventsViewModel // Import correct ViewModel

class FinishedEventsFragment : Fragment() {

    private var _binding: FragmentFinishedEventsBinding? = null
    private val binding get() = _binding!!

    // Instantiate ViewModel using factory
    private val eventsViewModel: EventsViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity().application)
    }
    private lateinit var eventAdapter: EventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        // Trigger fetch if needed
        eventsViewModel.fetchFinishedEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventListAdapter { event ->
            val action = FinishedEventsFragmentDirections.actionFinishedEventsToEventDetail(event.id)
            findNavController().navigate(action)
        }
        binding.rvFinishedEvents.adapter = eventAdapter
    }

    private fun observeViewModel() {
        eventsViewModel.finishedEvents.observe(viewLifecycleOwner) { resource ->
            binding.progressBarFinished.isVisible = resource is Resource.Loading
            binding.rvFinishedEvents.isVisible = resource is Resource.Success && !resource.data.isNullOrEmpty()
            binding.tvErrorFinished.isVisible = resource is Resource.Error || (resource is Resource.Success && resource.data.isNullOrEmpty())

            when (resource) {
                is Resource.Loading -> { /* Handled by visibility */ }
                is Resource.Success -> {
                    if (resource.data.isNullOrEmpty()) {
                        binding.tvErrorFinished.text = getString(R.string.no_finished_events)
                    } else {
                        eventAdapter.submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    binding.tvErrorFinished.text = resource.message ?: getString(R.string.error_loading_data)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvFinishedEvents.adapter = null
        _binding = null
    }
}
package com.example.eventappdicoding.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.eventappdicoding.R
import com.example.eventappdicoding.databinding.FragmentActiveEventsBinding // ViewBinding
import com.example.eventappdicoding.ui.adapter.EventListAdapter
import com.example.eventappdicoding.ui.viewmodel.EventsViewModel

class ActiveEventsFragment : Fragment() {

    private var _binding: FragmentActiveEventsBinding? = null
    private val binding get() = _binding!!

    private val eventsViewModel: EventsViewModel by viewModels()
    private lateinit var eventAdapter: EventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // Panggil fetch data jika belum ada data atau jika ada error sebelumnya
        // This ensures data is fetched on first load or if a previous attempt failed.
        if (eventsViewModel.activeEvents.value == null || eventsViewModel.errorActive.value != null) {
            eventsViewModel.fetchActiveEvents()
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventListAdapter { event ->
            val action = ActiveEventsFragmentDirections.actionActiveEventsToEventDetail(event.id)
            findNavController().navigate(action)
        }
        binding.rvActiveEvents.adapter = eventAdapter
    }

    private fun observeViewModel() {
        eventsViewModel.isLoadingActive.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarActive.isVisible = isLoading
            // Hide content and error message when loading
            if (isLoading) {
                binding.rvActiveEvents.isVisible = false
                binding.tvErrorActive.isVisible = false
            } else {
                binding.rvActiveEvents.isVisible = true
                binding.tvErrorActive.isVisible = true
            }
        }

        eventsViewModel.errorActive.observe(viewLifecycleOwner) { error ->
            // Show error only if not loading
            val showError = error != null && eventsViewModel.isLoadingActive.value == false
            binding.tvErrorActive.isVisible = showError
            if (showError) {
                binding.tvErrorActive.text = error // Display the error message from ViewModel
                binding.rvActiveEvents.isVisible = false // Hide list on error
            }
        }

        eventsViewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
            // Handle visibility after loading and error checks are done
            val isLoading = eventsViewModel.isLoadingActive.value ?: false
            val error = eventsViewModel.errorActive.value
            if (!isLoading && error == null) {
                if (events.isNullOrEmpty()) {
                    // Show "No events" message if list is empty and no error occurred
                    binding.tvErrorActive.isVisible = true
                    binding.tvErrorActive.text = getString(R.string.no_active_events)
                    binding.rvActiveEvents.isVisible = false
                } else {
                    // Show list if data is present, not loading, and no error
                    binding.tvErrorActive.isVisible = false
                    binding.rvActiveEvents.isVisible = true
                }
            } else if (error != null) {
                // Ensure list stays hidden if there was an error (already handled in error observer, but safe)
                binding.rvActiveEvents.isVisible = false
            }
            // If loading, visibility is handled by isLoading observer
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvActiveEvents.adapter = null // Prevent memory leaks
        _binding = null
    }
}
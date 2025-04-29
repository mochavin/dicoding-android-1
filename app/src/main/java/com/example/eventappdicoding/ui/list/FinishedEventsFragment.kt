package com.example.eventappdicoding.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.eventappdicoding.R
import com.example.eventappdicoding.databinding.FragmentFinishedEventsBinding // ViewBinding
import com.example.eventappdicoding.ui.adapter.EventListAdapter
import com.example.eventappdicoding.ui.viewmodel.EventsViewModel

class FinishedEventsFragment : Fragment() {

    private var _binding: FragmentFinishedEventsBinding? = null
    private val binding get() = _binding!!

    private val eventsViewModel: EventsViewModel by viewModels()
    private lateinit var eventAdapter: EventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        if (eventsViewModel.finishedEvents.value == null) {
            eventsViewModel.fetchFinishedEvents()
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventListAdapter { event ->
            // Navigasi ke detail dengan mengirim eventId
            val action = FinishedEventsFragmentDirections.actionFinishedEventsToEventDetail(event.id)
            findNavController().navigate(action)
        }
        binding.rvFinishedEvents.adapter = eventAdapter
    }

    private fun observeViewModel() {
        eventsViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
            binding.tvErrorFinished.isVisible = events.isNullOrEmpty() && !eventsViewModel.isLoadingFinished.value!! && eventsViewModel.errorFinished.value == null
            if(binding.tvErrorFinished.isVisible) {
                binding.tvErrorFinished.text = getString(R.string.no_finished_events)
            }
        }

        eventsViewModel.isLoadingFinished.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarFinished.isVisible = isLoading
            binding.rvFinishedEvents.isVisible = !isLoading && eventsViewModel.errorFinished.value == null
        }

        eventsViewModel.errorFinished.observe(viewLifecycleOwner) { error ->
            binding.tvErrorFinished.isVisible = error != null
            if(error != null) {
                binding.tvErrorFinished.text = error
            }
            binding.rvFinishedEvents.isVisible = error == null && !eventsViewModel.isLoadingFinished.value!!
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
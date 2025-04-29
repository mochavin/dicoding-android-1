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
import com.example.eventappdicoding.databinding.FragmentActiveEventsBinding // ViewBinding
import com.example.eventappdicoding.ui.adapter.EventListAdapter
import com.example.eventappdicoding.ui.viewmodel.EventsViewModel

class ActiveEventsFragment : Fragment() {

    private var _binding: FragmentActiveEventsBinding? = null
    private val binding get() = _binding!!

    // Gunakan activityViewModels jika ingin share ViewModel antar fragment di activity yg sama
    // atau viewModels() jika ViewModel spesifik untuk fragment ini
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

        // Panggil fetch data jika belum ada data
        if (eventsViewModel.activeEvents.value == null) {
            eventsViewModel.fetchActiveEvents()
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventListAdapter { event ->
            // Navigasi ke detail dengan mengirim eventId
            val action = ActiveEventsFragmentDirections.actionActiveEventsToEventDetail(event.id)
            findNavController().navigate(action)
        }
        binding.rvActiveEvents.adapter = eventAdapter
        // LayoutManager sudah diset di XML, tapi bisa juga diset di sini:
        // binding.rvActiveEvents.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        eventsViewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
            // Tampilkan pesan jika list kosong setelah load selesai
            binding.tvErrorActive.isVisible = events.isNullOrEmpty() && !eventsViewModel.isLoadingActive.value!! && eventsViewModel.errorActive.value == null
            if(binding.tvErrorActive.isVisible) {
                binding.tvErrorActive.text = getString(R.string.no_active_events)
            }
        }

        eventsViewModel.isLoadingActive.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarActive.isVisible = isLoading
            // Sembunyikan RecyclerView saat loading untuk menghindari tampilan kosong sesaat
            binding.rvActiveEvents.isVisible = !isLoading && eventsViewModel.errorActive.value == null
        }

        eventsViewModel.errorActive.observe(viewLifecycleOwner) { error ->
            binding.tvErrorActive.isVisible = error != null
            if(error != null) {
                binding.tvErrorActive.text = error
                // Optional: Tampilkan Toast juga
                // Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
            // Sembunyikan RecyclerView jika ada error
            binding.rvActiveEvents.isVisible = error == null && !eventsViewModel.isLoadingActive.value!!
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Hindari memory leak
    }
}
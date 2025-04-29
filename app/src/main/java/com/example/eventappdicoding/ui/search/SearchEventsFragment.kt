package com.example.eventappdicoding.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.eventappdicoding.R
import com.example.eventappdicoding.databinding.FragmentSearchEventsBinding
import com.example.eventappdicoding.ui.adapter.EventListAdapter

class SearchEventsFragment : Fragment() {

    private var _binding: FragmentSearchEventsBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchEventsViewModel by viewModels()
    private lateinit var searchAdapter: EventListAdapter
    private val args: SearchEventsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // Get query from arguments and trigger search
        val query = args.query
        if (savedInstanceState == null) { // Search only on initial creation, not on rotation
            searchViewModel.searchEvents(query)
        }
        // Optional: Set fragment title based on query
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.title_search_results) // Example title change

    }

    private fun setupRecyclerView() {
        searchAdapter = EventListAdapter { event ->
            // Navigate to detail from search results
            val action = SearchEventsFragmentDirections.actionSearchEventsFragmentToEventDetail(event.id)
            findNavController().navigate(action)
        }
        binding.rvSearchResults.adapter = searchAdapter
    }

    private fun observeViewModel() {
        searchViewModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchAdapter.submitList(results)
            binding.rvSearchResults.isVisible = !results.isNullOrEmpty()
            // Check for the "no results" specific error case
            if (results.isNullOrEmpty() && searchViewModel.error.value == "no_results" && !searchViewModel.isLoading.value!!) {
                binding.tvErrorSearch.text = getString(R.string.no_search_results)
                binding.tvErrorSearch.isVisible = true
                binding.rvSearchResults.isVisible = false // Ensure RV is hidden
            } else if (results.isNullOrEmpty() && searchViewModel.error.value == null && !searchViewModel.isLoading.value!!) {
                // Hide error if list is empty but no error occurred (e.g., blank search initially)
                binding.tvErrorSearch.isVisible = false
            }
        }

        searchViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarSearch.isVisible = isLoading
            if (isLoading) {
                binding.rvSearchResults.isVisible = false
                binding.tvErrorSearch.isVisible = false // Hide error during load
            }
        }

        searchViewModel.error.observe(viewLifecycleOwner) { error ->
            val showError = error != null && error != "no_results" && !searchViewModel.isLoading.value!!
            binding.tvErrorSearch.isVisible = showError
            binding.rvSearchResults.isVisible = error == null && !searchViewModel.isLoading.value!!

            if (showError) {
                binding.tvErrorSearch.text = error ?: getString(R.string.error_search)
            } else if (error == "no_results" && !searchViewModel.isLoading.value!!) {
                // Handle "no results" case (already handled in searchResults observer, but keep consistent)
                binding.tvErrorSearch.text = getString(R.string.no_search_results)
                binding.tvErrorSearch.isVisible = true
                binding.rvSearchResults.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvSearchResults.adapter = null // Clear adapter reference
        _binding = null
    }
}
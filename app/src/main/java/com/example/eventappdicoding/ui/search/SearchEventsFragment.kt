package com.example.eventappdicoding.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Use this import
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.eventappdicoding.R
import com.example.eventappdicoding.data.Resource // Import Resource
import com.example.eventappdicoding.databinding.FragmentSearchEventsBinding
import com.example.eventappdicoding.di.ViewModelFactory // Import Factory
import com.example.eventappdicoding.ui.adapter.EventListAdapter
// Import correct ViewModel if package changed, assumed it's still here
// import com.example.eventappdicoding.ui.search.SearchEventsViewModel

class SearchEventsFragment : Fragment() {

    private var _binding: FragmentSearchEventsBinding? = null
    private val binding get() = _binding!!

    // Instantiate ViewModel using factory
    private val searchViewModel: SearchEventsViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity().application)
    }
    private lateinit var searchAdapter: EventListAdapter
    private val args: SearchEventsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        val query = args.query
        // Search only on initial creation or if query changes and isn't blank
        // ViewModel internal logic prevents re-searching same query if loading/success
        if (savedInstanceState == null && query.isNotBlank()) {
            searchViewModel.searchEvents(query)
        }

        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.title_search_results) + ": \"${query}\""
    }

    private fun setupRecyclerView() {
        searchAdapter = EventListAdapter { event ->
            val action = SearchEventsFragmentDirections.actionSearchEventsFragmentToEventDetail(event.id)
            findNavController().navigate(action)
        }
        binding.rvSearchResults.adapter = searchAdapter
    }

    private fun observeViewModel() {
        searchViewModel.searchResults.observe(viewLifecycleOwner) { resource ->
            binding.progressBarSearch.isVisible = resource is Resource.Loading
            binding.rvSearchResults.isVisible = resource is Resource.Success && !resource.data.isNullOrEmpty()
            // Show error text view on Error OR if Success but the list is empty
            binding.tvErrorSearch.isVisible = resource is Resource.Error || (resource is Resource.Success && resource.data.isNullOrEmpty())

            when (resource) {
                is Resource.Loading -> { /* Handled by visibility */ }
                is Resource.Success -> {
                    if (resource.data.isNullOrEmpty()) {
                        // Use specific "no results" string
                        binding.tvErrorSearch.text = getString(R.string.no_search_results)
                    } else {
                        searchAdapter.submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    binding.tvErrorSearch.text = resource.message ?: getString(R.string.error_search)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvSearchResults.adapter = null
        _binding = null
    }
}
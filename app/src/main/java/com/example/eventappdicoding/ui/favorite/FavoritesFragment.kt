package com.example.eventappdicoding.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.eventappdicoding.databinding.FragmentFavoritesBinding
import com.example.eventappdicoding.di.ViewModelFactory
import com.example.eventappdicoding.ui.adapter.FavoriteAdapter // Import FavoriteAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity().application)
    }
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter { favoriteEvent ->
            // Navigate to detail screen when a favorite item is clicked
            val action = FavoritesFragmentDirections.actionNavigationFavoritesToEventDetail(favoriteEvent.id)
            findNavController().navigate(action)
        }
        binding.rvFavoriteEvents.adapter = favoriteAdapter
        // LayoutManager is set in XML
    }

    private fun observeViewModel() {
        favoriteViewModel.allFavoriteEvents.observe(viewLifecycleOwner) { favorites ->
            val isEmpty = favorites.isNullOrEmpty()
            binding.rvFavoriteEvents.isVisible = !isEmpty
            binding.tvEmptyFavorites.isVisible = isEmpty

            if (!isEmpty) {
                favoriteAdapter.submitList(favorites)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvFavoriteEvents.adapter = null // Clean up adapter
        _binding = null
    }
}
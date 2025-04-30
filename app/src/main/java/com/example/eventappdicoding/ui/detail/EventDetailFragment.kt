package com.example.eventappdicoding.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Use this import
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.eventappdicoding.R
import com.example.eventappdicoding.data.Resource // Import Resource
import com.example.eventappdicoding.data.model.EventDetail
import com.example.eventappdicoding.databinding.FragmentEventDetailBinding
import com.example.eventappdicoding.di.ViewModelFactory // Import Factory
import com.example.eventappdicoding.ui.viewmodel.EventDetailViewModel // Import correct ViewModel

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    // Instantiate ViewModel using factory
    private val viewModel: EventDetailViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity().application)
    }
    private val args: EventDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = args.eventId
        // Set the event ID in the ViewModel
        viewModel.setEventId(eventId)

        observeViewModel()
        setupLinkButton()
        setupFavoriteButton()
    }

    private fun observeViewModel() {
        viewModel.eventDetail.observe(viewLifecycleOwner) { resource ->
            // Show content only on Success with non-null data
            // Visibility handled below based on resource type

            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarDetail.isVisible = true
                    binding.contentScrollView.isVisible = false
                    binding.tvErrorDetail.isVisible = false
                    binding.fabFavorite.isVisible = false // Hide FAB while loading
                }
                is Resource.Success -> {
                    binding.progressBarDetail.isVisible = false
                    resource.data?.let {
                        bindEventDetail(it)
                        binding.contentScrollView.isVisible = true
                        binding.tvErrorDetail.isVisible = false
                        binding.fabFavorite.isVisible = true // Show FAB on success
                    } ?: run {
                        // Handle case where success is called but data is somehow null
                        binding.tvErrorDetail.text = getString(R.string.error_loading_detail) + " (Data null)"
                        binding.tvErrorDetail.isVisible = true
                        binding.contentScrollView.isVisible = false
                        binding.fabFavorite.isVisible = false
                    }
                }
                is Resource.Error -> {
                    binding.progressBarDetail.isVisible = false
                    binding.contentScrollView.isVisible = false
                    binding.tvErrorDetail.text = resource.message ?: getString(R.string.error_loading_detail)
                    binding.tvErrorDetail.isVisible = true
                    binding.fabFavorite.isVisible = false
                }
            }
        }

        // Observe favorite status
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            updateFavoriteButtonIcon(isFavorite)
        }
    }

    private fun bindEventDetail(detail: EventDetail) {
        binding.ivDetailImage.load(detail.displayImageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_image_placeholder)
            error(R.drawable.ic_broken_image)
        }
        binding.tvDetailName.text = detail.name
        binding.tvDetailOrganizer.text = detail.ownerName
        binding.tvDetailTime.text = detail.getFormattedBeginTime()
        binding.tvDetailQuota.text = getString(R.string.remaining_quota_format, detail.remainingQuota)
        binding.tvDetailDescription.text = Html.fromHtml(detail.description, Html.FROM_HTML_MODE_COMPACT) // Use COMPACT for newer APIs

        // Update button state based on the fetched detail
        binding.btnOpenLink.isEnabled = detail.link.isNotEmpty()
        if (detail.link.isEmpty()){
            binding.btnOpenLink.text = getString(R.string.no_link_available)
        } else {
            binding.btnOpenLink.text = getString(R.string.open_event_link)
        }
    }

    private fun setupFavoriteButton() {
        binding.fabFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    private fun updateFavoriteButtonIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        binding.fabFavorite.setImageResource(iconRes)
        // Optional: Change FAB background tint if needed
        // val tintColor = ContextCompat.getColor(requireContext(), if (isFavorite) R.color.red else R.color.gray)
        // binding.fabFavorite.backgroundTintList = ColorStateList.valueOf(tintColor)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun setupLinkButton() {
        binding.btnOpenLink.setOnClickListener {
            // Get URL from the *currently observed data* if available
            val currentData = (viewModel.eventDetail.value as? Resource.Success)?.data
            currentData?.link?.let { url ->
                if (url.isNotEmpty()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        if (intent.resolveActivity(requireActivity().packageManager) != null) {
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.failed_to_open_link) + ": No app can handle this link", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("EventDetailFragment", "Error opening link", e)
                        Toast.makeText(requireContext(), getString(R.string.failed_to_open_link) + ": " + e.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.no_link_available), Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), getString(R.string.link_info_not_loaded), Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
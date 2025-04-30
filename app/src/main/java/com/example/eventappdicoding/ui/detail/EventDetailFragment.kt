package com.example.eventappdicoding.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        observeViewModel()
        // Fetch data only if not already fetched or if ID changes (ViewModel handles this)
        viewModel.fetchEventDetail(eventId)

        setupLinkButton() // Can be set up regardless of data state
    }

    private fun observeViewModel() {
        viewModel.eventDetail.observe(viewLifecycleOwner) { resource ->
            binding.progressBarDetail.isVisible = resource is Resource.Loading
            // Show content only on Success with non-null data
            binding.contentScrollView.isVisible = resource is Resource.Success && resource.data != null
            binding.tvErrorDetail.isVisible = resource is Resource.Error

            when (resource) {
                is Resource.Loading -> {
                    // Optional: Clear previous data?
                    // binding.contentScrollView.isVisible = false
                    // binding.tvErrorDetail.isVisible = false
                }
                is Resource.Success -> {
                    resource.data?.let {
                        bindEventDetail(it)
                    } ?: run {
                        // Handle case where success is called but data is somehow null
                        binding.tvErrorDetail.text = getString(R.string.error_loading_detail) + " (Data null)"
                        binding.tvErrorDetail.isVisible = true
                        binding.contentScrollView.isVisible = false
                    }
                }
                is Resource.Error -> {
                    binding.tvErrorDetail.text = resource.message ?: getString(R.string.error_loading_detail)
                    binding.contentScrollView.isVisible = false
                }
            }
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

    @SuppressLint("QueryPermissionsNeeded")
    private fun setupLinkButton() {
        binding.btnOpenLink.setOnClickListener {
            // Get URL from the *currently bound data* if available
            val currentData = (viewModel.eventDetail.value as? Resource.Success)?.data
            currentData?.link?.let { url ->
                if (url.isNotEmpty()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        // No need to manually add http/https, toUri handles common schemes
                        if (intent.resolveActivity(requireActivity().packageManager) != null) {
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.failed_to_open_link) + ": No app can handle this link", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) { // Catch specific exceptions if needed (e.g., ActivityNotFoundException)
                        Toast.makeText(requireContext(), getString(R.string.failed_to_open_link) + ": " + e.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // This case might be redundant if button is disabled, but safe to have
                    Toast.makeText(requireContext(), getString(R.string.no_link_available), Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                // Handle case where data isn't loaded yet or link is unavailable
                Toast.makeText(requireContext(), getString(R.string.no_link_available), Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
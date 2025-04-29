package com.example.eventappdicoding.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.eventappdicoding.R
import com.example.eventappdicoding.data.model.EventDetail
import com.example.eventappdicoding.databinding.FragmentEventDetailBinding // ViewBinding
import com.example.eventappdicoding.ui.viewmodel.EventDetailViewModel
import androidx.core.net.toUri

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventDetailViewModel by viewModels()
    private val args: EventDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = args.eventId // Ambil ID event dari argumen
        observeViewModel()
        viewModel.fetchEventDetail(eventId) // Minta ViewModel untuk fetch data

        setupLinkButton()
    }

    private fun observeViewModel() {
        viewModel.eventDetail.observe(viewLifecycleOwner) { detail ->
            binding.contentScrollView.isVisible = detail != null
            detail?.let { bindEventDetail(it) }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarDetail.isVisible = isLoading
            // Sembunyikan konten saat loading
            if(isLoading) {
                binding.contentScrollView.isVisible = false
                binding.tvErrorDetail.isVisible = false
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.tvErrorDetail.isVisible = error != null
            binding.contentScrollView.isVisible = error == null && !viewModel.isLoading.value!! // Sembunyikan konten jika error
            if (error != null) {
                binding.tvErrorDetail.text = error
                // Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
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
        binding.tvDetailTime.text = detail.getFormattedBeginTime() // Gunakan helper format waktu
        binding.tvDetailQuota.text = getString(R.string.remaining_quota_format, detail.remainingQuota)
        binding.tvDetailDescription.text = Html.fromHtml(detail.description, Html.FROM_HTML_MODE_LEGACY)

        // Handle visibilitas tombol link
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
            viewModel.eventDetail.value?.link?.let { url ->
                if (url.isNotEmpty()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        // Tambahkan http/https jika belum ada
                        if (intent.resolveActivity(requireActivity().packageManager) != null) {
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.failed_to_open_link) + ": No app can handle this link", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), getString(R.string.failed_to_open_link) + ": " + e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
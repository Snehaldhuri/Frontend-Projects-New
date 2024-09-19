package com.diipl.moviebeam.ui.guestservice.concierge

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.toiletryResponse.ToiletryResponse
import com.diipl.moviebeam.databinding.FragmentToiletryRequestBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.ui.guestservice.GuestServiceViewModel
import com.diipl.moviebeam.ui.guestservice.concierge.laundry.LaundryRequestErrorFragment
import com.diipl.moviebeam.ui.guestservice.concierge.laundry.LaundryRequestSummaryFragment
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.showToast
import com.google.android.material.snackbar.Snackbar

class ToiletryRequestFragment(
    private var onOkClicked: () -> Unit
) : BaseFragment() {

    private var _binding: FragmentToiletryRequestBinding? = null
    val binding get() = _binding!!
    private val selectedMenuItemPosition = 0
    private val guestServiceViewModel: GuestServiceViewModel by activityViewModels()

    private lateinit var toiletryDetailResponse: ToiletryResponse
    lateinit var toiletry_list: List<ToiletryResponse.ToiletryData>

    override fun observeViewModel() {
        observeToast(guestServiceViewModel.showToast)
    }

    override fun initViewBinding() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToiletryRequestBinding.inflate(inflater, container, false)
        setToiletryDetailData()
        return binding.root
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    fun setToiletryData(toiletryData: ToiletryResponse) {
        this.toiletryDetailResponse = toiletryData
    }

    fun setToiletryDetailData() {
        toiletry_list = toiletryDetailResponse.toiletryDataList

        if (_binding != null) {

            binding.rvToiletryRequest.layoutManager =
                GridLayoutManager(context, 2)

            // Initialize adapter with onQuantityChanged callback
            val toiletryRequestAdapter = ToiletryRequestAdapter(
                onMenuItemClicked = { isVisible, item -> },
                onQuantityChanged = { updateSelectedItems() }
            )
            toiletryRequestAdapter.setToiletryList(toiletry_list)
            binding.rvToiletryRequest.adapter = toiletryRequestAdapter
            binding.rvToiletryRequest.adapter = toiletryRequestAdapter

            binding.rvToiletryRequest.post {
                binding.rvToiletryRequest.findViewHolderForAdapterPosition(
                    selectedMenuItemPosition
                )?.itemView?.requestFocus()
            }
            binding.btnCancel.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.btnCancel.background = getGradientColor()
                    view.setOnKeyListener { _, keycode, keyEvent ->
                        if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                            when (keycode) {
                                KeyEvent.KEYCODE_DPAD_UP -> {
//                                        binding.rvToiletryRequest.postDelayed({
//                                            binding.rvToiletryRequest.requestFocus()
//                                            binding.rvToiletryRequest.smoothScrollToPosition(selectedMenuItemPosition);
//                                            binding.rvToiletryRequest.findViewHolderForAdapterPosition(selectedMenuItemPosition)?.itemView?.requestFocus();
//                                        },1)
                                }
                            }
                        }
                        false
                    }
                } else {
                    binding.btnCancel.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                }
            }
            binding.btnSendRequest.handleFocusChange()
            binding.btnCancel.setOnClickListener {
                onOkClicked()
            }
            binding.btnSendRequest.setOnClickListener {

                updateSelectedItems()
            }

        } else {
            Log.e("LaundryFragment", "_binding is null")
        }
    }

    private fun updateSelectedItems() {
        val selectedItems =
            (binding.rvToiletryRequest.adapter as? ToiletryRequestAdapter)?.getSelectedItems()

        val isConciergeVisible = requireActivity().findViewById<View>(R.id.fv_concierge)?.visibility == View.VISIBLE

        if (!selectedItems.isNullOrEmpty()) {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val summaryFragment = ToiletryRequestSummaryFragment {
                onOkClicked()
            }
            summaryFragment.setItemList(selectedItems)

            if (isConciergeVisible) {
                fragmentTransaction.replace(R.id.fv_concierge, summaryFragment)
            } else {
                fragmentTransaction.replace(R.id.fv_tab_content, summaryFragment)
            }

            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val errorFragment = ToiletryRequestErrorFragment()

            if (isConciergeVisible) {
                fragmentTransaction.replace(R.id.fv_concierge, errorFragment)
            } else {
                fragmentTransaction.replace(R.id.fv_tab_content, errorFragment)
            }

            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

}

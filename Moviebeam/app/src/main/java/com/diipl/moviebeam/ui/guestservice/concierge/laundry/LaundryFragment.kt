package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryDataResponse
import com.diipl.moviebeam.databinding.FragmentLaundryBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.handleFocusChange
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LaundryFragment(
    private var onOkClicked: () -> Unit
) : BaseFragment() {

    lateinit var laundry_adapter: LaundryAdapter
    private lateinit var customAdapterLaundry: CustomAdapterLaundry
    private lateinit var binding: FragmentLaundryBinding
    private var laundryHeaderPosition: Int = 0
    private lateinit var laundryDetailResponse: LaundryDataResponse

    override fun observeViewModel() {}

    override fun initViewBinding() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLaundryBinding.inflate(inflater, container, false)

        binding.rvLaundry.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.lvLaundry.layoutManager =
            GridLayoutManager(context, 2)

        val cardRecyclerView2: RecyclerView = binding.rvLaundry

        cardRecyclerView2.layoutManager = LinearLayoutManager(context)


        val cardRecyclerView: RecyclerView = binding.rvLaundry
        cardRecyclerView.layoutManager = LinearLayoutManager(context)


        binding.btnLaundryCancel.handleFocusChange()
        binding.btnLaundrySendRequest.handleFocusChange()
        binding.btnLaundryCancel.setOnClickListener {
            onOkClicked()
        }
        customAdapterLaundry = CustomAdapterLaundry(
            onMenuItemFocused = { },
            onLeftKeyPressed = {
                binding.lvLaundry.smoothScrollToPosition(laundryHeaderPosition)
            }
        )
        customAdapterLaundry.clearSelectedItems()
        setLaundryDetailData()

        binding.btnLaundrySendRequest.setOnClickListener {
            updateSelectedItems()
        }
        return binding.root
    }

    fun setLaundryData(laundryData: LaundryDataResponse) {
        this.laundryDetailResponse = laundryData
    }

    private fun setLaundryDetailData() {
        // Check if _binding is initialized
        lifecycleScope.launch {
            delay(100)
            customAdapterLaundry = CustomAdapterLaundry(
                onMenuItemFocused = { },
                onLeftKeyPressed = {
                    binding.lvLaundry.smoothScrollToPosition(laundryHeaderPosition)
                }
            )
            binding.lvLaundry.adapter = customAdapterLaundry

            laundry_adapter = LaundryAdapter(
                onMenuItemFocused = {
                    customAdapterLaundry = CustomAdapterLaundry(
                        onMenuItemFocused = { },
                        onLeftKeyPressed = {
                            binding.lvLaundry.smoothScrollToPosition(laundryHeaderPosition)
                        }
                    )
                    binding.lvLaundry.adapter = customAdapterLaundry
                    customAdapterLaundry.setNewsList(it.subCategoryList)
                },
                onLeftKeyPressed = {},
                onRightKeyPressed = {
                    binding.rvLaundry.clearFocus()
                    binding.lvLaundry.requestFocus()
                    binding.lvLaundry.getChildAdapterPosition(binding.lvLaundry.focusedChild)
                }
            )
            laundryDetailResponse.laundryDataList.let {
                laundry_adapter.setLaundryList(it)
            }
            binding.rvLaundry.adapter = laundry_adapter


        }
    }

    private fun updateSelectedItems() {

        val selectedItems = (binding.lvLaundry.adapter as? CustomAdapterLaundry)?.getSelectedItems()
        Log.d("selectedItems", "updateSelectedItems: $selectedItems")

        val isConciergeVisible = requireActivity().findViewById<View>(R.id.fv_concierge)?.visibility == View.VISIBLE

        if (!selectedItems.isNullOrEmpty()) {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val summaryFragment = LaundryRequestSummaryFragment {
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
            val errorFragment = LaundryRequestErrorFragment()

            // Conditionally replace fragment based on fv_concierge visibility
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


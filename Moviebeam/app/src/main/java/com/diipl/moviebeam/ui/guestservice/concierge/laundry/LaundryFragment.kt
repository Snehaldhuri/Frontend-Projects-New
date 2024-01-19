package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryDataList
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryResponce
import com.diipl.moviebeam.databinding.FragmentLaundryBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.observe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LaundryFragment : BaseFragment() {
    private val laundryViewModel: LaundryViewModel by viewModels()
    lateinit var serail_num: String
    lateinit var laundry_adapter: LaundryAdapter
    lateinit var laundry_list: List<LaundryDataList>

    private var gradientStartColor: String? = null
    private var gradientEndColor: String? = null

    private var _binding: FragmentLaundryBinding? = null
    val binding get() = _binding!!
    override fun observeViewModel() {
        observe(laundryViewModel.laundryMasterLiveData, ::handleLaundryMasterResponse)
    }

    override fun initViewBinding() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLaundryBinding.inflate(inflater, container, false)

        binding.rvLaundry.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val cardRecyclerView: RecyclerView = binding.rvLaundry
        cardRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }


    fun handleLaundryMasterResponse(status: Resource<LaundryResponce>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {

                Log.e(
                    "data_laundry",
                    "handleLaundryMasterResponse: ${laundryViewModel.laundryMasterLiveData.value?.data}",
                )
                laundryViewModel.laundryMasterLiveData.value?.data?.let {
                    Log.d("responce_laundryMaster", "handleStbMasterResponse:${it}")
                    laundry_list = it.laundryDataList
                    //laundry_adapter = LaundryAdapter()
                    //code for default list view
                    var size = laundry_list[0].subCategoryList
                    var array_title = ArrayList<String>()
                    var array_price = ArrayList<String>()
                    var adp: CustomAdapterLaundry

                    for (i in 0 until size.size) {
                        array_title.add(laundry_list[0].subCategoryList[i].title!!)
                        array_price.add(laundry_list[0].subCategoryList[i].price.toString())
                    }
                    adp = CustomAdapterLaundry(requireContext(), array_title, array_price)
                    binding.lvLaundry.adapter = adp




                    laundry_adapter = LaundryAdapter(onMenuItemFocused = {
                        var size = it.subCategoryList
                        var array_title = ArrayList<String>()
                        var array_price = ArrayList<String>()
                        var adp: CustomAdapterLaundry






                        for (i in 0 until size.size) {
                            array_title.add(it.subCategoryList[i].title!!)
                            array_price.add(it.subCategoryList[i].price.toString())
                        }
                        adp = CustomAdapterLaundry(requireContext(), array_title, array_price)
                        binding.lvLaundry.adapter = adp


                    }, onLeftKeyPressed = {
                        // binding.rvNewsHeader.layoutManager?.scrollToPosition(newsHeaderPosition)
                    })

                    laundry_list?.let {
                        laundry_adapter.setNewsList(it)
                    }
                    laundry_adapter.setGradient(getGradient())
                    binding.rvLaundry.adapter = laundry_adapter
                }
                // binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { laundryViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { laundryViewModel.showToastMessage(it) }
            }
        }
    }


    fun setGradientColor(startColor: String, endColor: String) {
        gradientStartColor = startColor
        gradientEndColor = endColor
    }

    private fun getGradient(
    ): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )
        gradientDrawable.cornerRadius = 10f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }
}

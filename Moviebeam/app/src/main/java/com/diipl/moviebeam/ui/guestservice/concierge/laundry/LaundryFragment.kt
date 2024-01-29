package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryRequestDTO
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryResponse
import com.diipl.moviebeam.data.dto.laundryResponce.SubCategoryList
import com.diipl.moviebeam.databinding.FragmentLaundryBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.observe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LaundryFragment : BaseFragment() {
    private val laundryViewModel: LaundryViewModel by viewModels()
    lateinit var serail_num: String
    lateinit var laundry_adapter: LaundryAdapter
    lateinit var customAdapterLaundry: CustomAdapterLaundry

    lateinit var laundry_list: List<LaundryRequestDTO>

    private var gradientStartColor: String? = null
    private var gradientEndColor: String? = null

    private var _binding: FragmentLaundryBinding? = null
    val binding get() = _binding!!
    private var laundryHeaderPosition: Int = 0
    private var laundrySubCategoryPosition: Int = 0
    private var selectedItems: MutableList<SubCategoryList> = mutableListOf()


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

        binding.lvLaundry.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val cardRecyclerView2: RecyclerView = binding.rvLaundry

        cardRecyclerView2.layoutManager = LinearLayoutManager(context)


        val cardRecyclerView: RecyclerView = binding.rvLaundry
        cardRecyclerView.layoutManager = LinearLayoutManager(context)


        binding.btnLaundryCancel.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setFocus(binding.btnLaundryCancel)
            } else {
                binding.btnLaundryCancel.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnLaundrySendRequest.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setFocus(binding.btnLaundrySendRequest)
            } else {
                binding.btnLaundrySendRequest.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }



        binding.btnLaundrySendRequest.setOnClickListener {

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val summaryFragment = LaundryRequestSummaryFragment {
//                        view?.requestFocus()
//                        view?.performClick()
            }
            val mBundle = Bundle()
            mBundle.putString("gradientStartColor", gradientStartColor)
            mBundle.putString("gradientEndColor", gradientEndColor)
            summaryFragment.arguments = mBundle
            summaryFragment.setItemList(selectedItems)

            fragmentTransaction.replace(
                R.id.fv_tab_content,
                summaryFragment
            )
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
            Log.d("TAG1212", "handleAccountSetupResponse: $selectedItems")
        }
        return binding.root
    }


    fun handleLaundryMasterResponse(status: Resource<LaundryResponse>) {
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
                    var subCategoryList: ArrayList<SubCategoryList> = arrayListOf()
                    var array_title = ArrayList<String>()
                    var array_price = ArrayList<Double>()


                    laundry_adapter = LaundryAdapter(onMenuItemFocused = {item ->
                        Log.e("size_sub", "handleLaundryMasterResponse:${item.subCategoryList.size}")
                        for (i in 0 until item.subCategoryList.size) {
                            Log.e(
                                "size_of_title",
                                "handleLaundryMasterResponse:${item.subCategoryList[i].title}",
                            )
                        }
                    }, onLeftKeyPressed = {},
                        onRightKeyPressed = {
                            binding.lvLaundry.scrollToPosition(0)
                        })
                    laundry_list.let {
                        laundry_adapter.setNewsList(it)
                    }
                    laundry_adapter.setGradient(getGradient())
                    binding.rvLaundry.adapter = laundry_adapter
                }
            }

            else -> {
                status.errorCode?.let { laundryViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { laundryViewModel.showToastMessage(it) }
            }
        }
    }


    private fun setFocus(cardView: Button) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        cardView.background = gradientDrawable
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

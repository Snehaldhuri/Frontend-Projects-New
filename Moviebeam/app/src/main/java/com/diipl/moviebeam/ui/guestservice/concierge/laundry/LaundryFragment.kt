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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryCategory
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryDataList
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryDataResponse
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryResponce
import com.diipl.moviebeam.databinding.FragmentLaundryBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.observe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LaundryFragment : BaseFragment() {
    private val laundryViewModel: LaundryViewModel by viewModels()

    lateinit var laundry_adapter: LaundryAdapter
    lateinit var customAdapterLaundry: CustomAdapterLaundry
    lateinit var laundry_list: List<LaundryCategory>
    private var gradientStartColor: String? = null
    private var gradientEndColor: String? = null
    private var _binding: FragmentLaundryBinding? = null
    val binding get() = _binding!!
    private var laundryHeaderPosition: Int = 0
    private var laundrySubCategoryPosition: Int = 0
    private var selectedItems: MutableList<LaundryResponce> = mutableListOf()
    private lateinit var laundryDetailResponse: LaundryDataResponse


    override fun observeViewModel() {
//        observe(laundryViewModel.laundryMasterLiveData, ::handleLaundryMasterResponse)
    }

    override fun initViewBinding() {
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
            GridLayoutManager(context, 2)

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
        setLaundryDetailData()

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

    fun setLaundryData(laundryData: LaundryDataResponse) {
        this.laundryDetailResponse = laundryData
    }
    fun setLaundryDetailData() {
        laundry_list = laundryDetailResponse.laundryDataList

        // Check if _binding is initialized
        if (_binding != null) {
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
                    customAdapterLaundry.setGradientColor(gradientStartColor!!, gradientEndColor!!)
                    customAdapterLaundry.setGradient(getGradient())
                },
                onLeftKeyPressed = {},
                onRightKeyPressed = {
                    binding.rvLaundry.clearFocus()
                    binding.lvLaundry.requestFocus()
                    binding.lvLaundry.getChildAdapterPosition(binding.lvLaundry.getFocusedChild())
                }
            )
            laundry_list.let {
                laundry_adapter.setLaundryList(it)
            }
            laundry_adapter.setGradient(getGradient())
            binding.rvLaundry.adapter = laundry_adapter
        } else {
            Log.e("LaundryFragment", "_binding is null")
        }
    }

}


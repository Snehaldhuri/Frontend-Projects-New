package com.diipl.moviebeam.ui.guestservice.concierge

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.accountsetup.ItemMenu
import com.diipl.moviebeam.databinding.FragmentToiletryRequestBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.ui.guestservice.GuestServiceViewModel
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar

class ToiletryRequestFragment(
    private var onOkClicked: () -> Unit
) : BaseFragment() {

    private var _binding: FragmentToiletryRequestBinding? = null
    val binding get() = _binding!!
    private var gradientStartColor = ""
    private var gradientEndColor = ""

    private val guestServiceViewModel: GuestServiceViewModel by activityViewModels()

    private val selectedItems: MutableList<ItemMenu> = mutableListOf()

    override fun observeViewModel() {
        observe(guestServiceViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observeToast(guestServiceViewModel.showToast)
    }

    override fun initViewBinding() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentToiletryRequestBinding.inflate(inflater, container, false)
        arguments?.let {

            gradientStartColor = it.getString("gradientStartColor").toString()
            gradientEndColor = it.getString("gradientEndColor").toString()
        }
        return binding.root
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> { binding.loaderView.toVisible() }
            is Resource.Success -> {
                val response = guestServiceViewModel.accountSetupLiveData.value?.data?.itemMenuList
                binding.rvToiletryRequest.layoutManager = LinearLayoutManager(requireActivity())
                val toiletryRequestAdapter = ToiletryRequestAdapter{isVisible, item ->
                    if(isVisible){
                        selectedItems.remove(item)
                    }else{
                        selectedItems.add(item)
                        Log.d("selectedItems","selectedItems $selectedItems")
                    }
                }
                response?.let { toiletryRequestAdapter.setButtonList(it) }
                toiletryRequestAdapter.setGradientColor(gradientStartColor, gradientEndColor)
                binding.rvToiletryRequest.adapter = toiletryRequestAdapter

                binding.btnCancel.setOnFocusChangeListener { view, hasFocus ->
                    if(hasFocus){
                        setFocus(binding.btnCancel)
                    }
                    else{
                        binding.btnCancel.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                    }
                }
                binding.btnSendRequest.setOnFocusChangeListener { view, hasFocus ->
                    if(hasFocus){
                        setFocus(binding.btnSendRequest)
                    }
                    else{
                        binding.btnSendRequest.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                    }
                }
                binding.btnCancel.setOnClickListener {
                    onOkClicked()
                }
                binding.btnSendRequest.setOnClickListener {

                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    val summaryFragment = ToiletryRequestSummaryFragment{
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

                binding.loaderView.toInvisible()
            }
            else -> {
                status.errorMsg?.let { guestServiceViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
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
        this.gradientStartColor = startColor
        this.gradientEndColor = endColor
    }

}
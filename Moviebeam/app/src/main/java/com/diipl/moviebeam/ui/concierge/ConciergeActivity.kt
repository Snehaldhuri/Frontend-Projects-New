package com.diipl.moviebeam.ui.concierge

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.accountsetup.Concierge
import com.diipl.moviebeam.data.dto.btn.ConciergeBtnModel
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryDataResponse
import com.diipl.moviebeam.data.dto.toiletryResponse.ToiletryResponse
import com.diipl.moviebeam.databinding.ActivityConciergeBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.guestservice.concierge.ConciergeFragment
import com.diipl.moviebeam.ui.guestservice.concierge.GolfFragment
import com.diipl.moviebeam.ui.guestservice.concierge.LaundryTimeFragment
import com.diipl.moviebeam.ui.guestservice.concierge.MakeMyRoomFragment
import com.diipl.moviebeam.ui.guestservice.concierge.SpaFragment
import com.diipl.moviebeam.ui.guestservice.concierge.ToiletryRequestFragment
import com.diipl.moviebeam.ui.guestservice.concierge.ValetParkingFragment
import com.diipl.moviebeam.ui.guestservice.concierge.laundry.LaundryFragment
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ConciergeActivity : BaseActivity() {

    private lateinit var binding: ActivityConciergeBinding
    private val viewModel: ConciergeViewModel by viewModels()
    private var focusView: View? = null
    private var isSubCategoryOpen = false

    override fun observeViewModel() {
        observe(viewModel.hotelCustomizationLiveData, ::handleHotelCustomizationResponse)
    }

    override fun initViewBinding() {
        binding = ActivityConciergeBinding.inflate(layoutInflater)
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnBack.handleFocusChange()
        binding.btnBack.setOnClickListener { handleBackRemoteClick() }
    }

    private fun getConciergeFragment(): ConciergeFragment {
        return ConciergeFragment(onClick = { cView, conciergeService ->
            focusView = cView
            binding.tvServiceTitle.text = conciergeService.categoryName
            var fragment: Fragment? = null
            when (conciergeService.serviceId) {
                1 -> {
                    fragment = MakeMyRoomFragment {
                        handleBackRemoteClick()
                    }
                }

                2 -> {
                    fragment = ValetParkingFragment {
                        handleBackRemoteClick()
                    }
                }

                4 -> {
                    fragment = ToiletryRequestFragment() {
                        handleBackRemoteClick()
                    }
                    val toiletryData = readToiletryJson()
                    fragment.setToiletryData(toiletryData)

                }

                5 -> {
                    fragment = SpaFragment()
                }

                6 -> {
                    fragment = GolfFragment()
                }

                7 -> {
                    fragment = LaundryTimeFragment {
                        handleBackRemoteClick()
                    }
                }

                3 -> {
                    fragment = LaundryFragment() {
                        handleBackRemoteClick()
                    }
                    val laundryData = readLaundryJson()
                    if (laundryData != null) {
                        fragment.setLaundryData(laundryData)
                    }
                }
            }

            if (fragment != null) {
                changeFragment(fragment)
            }

            val conciergeListFromApi: List<Int>? =
                viewModel.hotelCustomizationLiveData.value?.data?.conciergeList?.map { concierge -> concierge.serviceId }
            val conciergeModelList: List<ConciergeBtnModel> =
                Constants.CONCIERGE_BUTTON_LIST.filter { concierge ->
                    conciergeListFromApi?.contains(concierge.serviceId) == true
                }

            fragment?.let {
                isSubCategoryOpen = true
                changeFragment(fragment)
            }
        },
//            onUpKeyPressed = {
//                binding.btnBack.requestFocus()
//            }
        ).also { it.arguments = Bundle().also { it.putInt("COLUMNS", 5) } }
    }

    private fun handleHotelCustomizationResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> {
                binding.loaderView.toVisible()
            }

            is Resource.Success -> {
                loadConcierge(status.data?.conciergeList)
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { viewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun loadConcierge(conciergeList: List<Concierge>?) {
        val conciergeFragment = getConciergeFragment()
        changeFragment(conciergeFragment)

        val conciergeListFromApi: List<Int?>? =
            conciergeList?.map { concierge -> concierge.serviceId }
        val conciergeModelList: List<ConciergeBtnModel> =
            Constants.CONCIERGE_BUTTON_LIST.filter { concierge ->
                conciergeListFromApi?.contains(concierge.serviceId) == true
            }

        conciergeFragment.setAdapter(
            conciergeModelList,
            focusView
        )
    }

    fun handleBackRemoteClick() {
        if (isSubCategoryOpen) {
            loadConcierge(viewModel.hotelCustomizationLiveData.value?.data?.conciergeList)
//            supportFragmentManager.popBackStack()
            isSubCategoryOpen = false
        } else {
            finish()
        }
    }
    private fun readToiletryJson(): ToiletryResponse {
        return try {
            val gson = Gson()
            val br = this.assets.open("ToiletryData.json").bufferedReader()
            val stringBuilder = StringBuilder()
            for (str in br.readLines()) {
                stringBuilder.append(str)
            }
            val data = JSONObject(stringBuilder.toString())
            gson.fromJson(data.toString(), ToiletryResponse::class.java)
        } catch (e: Exception) {
            logE("In GuestServiceActivity readToiletryJson:${e.message}")
            ToiletryResponse()
        }
    }
    private fun readLaundryJson(): LaundryDataResponse? {
        return try {
            val gson = Gson()
            val br = this.assets.open("LaundryData.json").bufferedReader()
            gson.fromJson(br, LaundryDataResponse::class.java)
        } catch (e: Exception) {
            logE("Exception in GuestServiceActivity readLaundryJson:${e.message}")
            null
        }
    }
    private fun changeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(TRANSIT_FRAGMENT_OPEN)
        transaction.replace(binding.fvConcierge.id, fragment)
        transaction.commitNow()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        handleBackRemoteClick()
    }

}
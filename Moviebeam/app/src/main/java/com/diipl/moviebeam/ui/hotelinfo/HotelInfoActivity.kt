package com.diipl.moviebeam.ui.hotelinfo


import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.hotelservice.TabListObj
import com.diipl.moviebeam.databinding.ActivityHotelInfoBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HotelInfoActivity : BaseActivity(), HotelInfoTabAdapter.OnFocusChangeListener {

    private val hotelInfoViewModel: HotelInfoViewModel by viewModels()

    private lateinit var binding: ActivityHotelInfoBinding
    private var helpInfoTabIndex = 0

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var hotelServicesDataStore: DataStore<HotelServiceResponse>

    private lateinit var adapter: HotelInfoTabAdapter
    private var focusedView: View? = null

    override fun observeViewModel() {
        observe(hotelInfoViewModel.hotelServiceLiveData, ::handleHotelServiceResponse)
        observeSnackBarMessages(hotelInfoViewModel.showSnackBar)
        observeToast(hotelInfoViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityHotelInfoBinding.inflate(layoutInflater)
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // fetch data from dataStore
        hotelInfoViewModel.getAccountSetupResponseData(accountSetupDataStore)
        hotelInfoViewModel.getHotelServicesResponseData(hotelServicesDataStore)

        binding.btnBack.handleFocusChange()

        binding.btnBack.setOnClickListener { finish() }
        binding.rvHotelInfoHeader.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun handleHotelServiceResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                status.data?.let {
//                    hotelInfoViewModel.setHotelServicesResponseData(it)
                    val tabMap = mutableMapOf<String, TabListObj>()
                    val tabs = mutableListOf<String>()
                    val response = it
                    for (service in response.servicesList) {
                        when (service.categoryName) {
                            "All" -> {
                                var helpInfoAdded = false
                                service.serviceList.forEach { s ->
                                    if (s.title == "Help & Info") {
                                        tabMap[s.title] =
                                            TabListObj(Constants.SERVICE_TYPE_HELP_INFO, s, null)
                                        tabs.add(s.title)
                                        helpInfoAdded = true
                                    } else {
                                        tabMap[s.title] =
                                            TabListObj(Constants.SERVICE_TYPE_SERVICE_INFO, s, null)
                                        tabs.add(s.title)
                                    }
                                }

                                if (!helpInfoAdded && service.contentTypeId == 15) {
                                    tabs.add(Constants.HELP_INFO)
                                    tabMap[Constants.HELP_INFO] =
                                        TabListObj(Constants.SERVICE_TYPE_HELP_INFO, null, null)
                                }
                            }

                            else -> {
                                tabMap[service.categoryName] = TabListObj(
                                    Constants.SERVICE_TYPE_CAROUSEL,
                                    null,
                                    service.serviceList
                                )
                                tabs.add(service.categoryName)
                            }
                        }
                    }

                    adapter = HotelInfoTabAdapter(
                        itemList = tabs,
                        onItemFocused = { it, view ->
                            focusedView = view
                            val transaction = supportFragmentManager.beginTransaction()
                            when (tabMap[it]?.serviceType) {
                                Constants.SERVICE_TYPE_CAROUSEL -> {
                                    binding.tvHeaderTitle.text =
                                        tabMap[it]?.serviceList?.get(0)?.title
                                    val carousel = CarouselListFragment(onItemFocused = { title ->
                                        binding.tvHeaderTitle.text = title
                                    }, onLeftKeyPressed = { title ->
                                        binding.rvHotelInfoHeader.post {
                                            binding.rvHotelInfoHeader.findContainingItemView(
                                                focusedView!!
                                            )?.requestFocus()
                                        }
                                        if (tabMap[it]?.serviceList?.get(0)?.title == title) {
                                            view.requestFocus()
                                        }
                                    })
                                    carousel.bindData(tabMap[it]?.serviceList)
                                    transaction.replace(R.id.fragment_container_carousel, carousel)
                                    transaction.commit()
                                }

                                Constants.SERVICE_TYPE_SERVICE_INFO -> {
                                    binding.tvHeaderTitle.text = it
                                    val bundle = Bundle()
                                    bundle.putString("title", it)
                                    tabMap[it]?.service?.description?.let { desc ->
                                        bundle.putString("desc", desc)
                                    }
                                    val list =
                                        if (tabMap[it]?.service?.serviceImageListCloud?.isNotEmpty() == true)
                                            tabMap[it]?.service?.serviceImageListCloud
                                        else
                                            tabMap[it]?.service?.serviceImageListNewCloud

                                    var imgUrl = "null"
                                    if (list!!.isNotEmpty()) {
                                        imgUrl = list[0]
                                    }
                                    bundle.putStringArrayList(
                                        Constants.SERVICE_IMAGE_LIST_PARAM,
                                        ArrayList(list)
                                    )
                                    bundle.putString("imgUrl", imgUrl)
                                    val fragment = HotelServiceInfoFragment()
                                    fragment.arguments = bundle
                                    transaction.replace(R.id.fragment_container_carousel, fragment)
                                    transaction.commit()
                                }

                                Constants.SERVICE_TYPE_HELP_INFO -> {
                                    binding.tvHeaderTitle.text = it
                                    val bundle = Bundle()
                                    bundle.putString("title", it)

                                    bundle.putString(
                                        "desc",
                                        hotelInfoViewModel.accountSetupLiveData.value?.data?.address
                                    )
                                    val fragment = HotelServiceInfoFragment()
                                    fragment.arguments = bundle
                                    transaction.replace(R.id.fragment_container_carousel, fragment)
                                    transaction.commit()
                                }

                                else -> {

                                    binding.tvHeaderTitle.text = it
                                    val bundle = Bundle()
                                    bundle.putString("title", it)
                                    bundle.putString(
                                        "desc",
                                        hotelInfoViewModel.accountSetupLiveData.value?.data?.address
                                    )
                                    val fragment = HotelServiceInfoFragment()
                                    fragment.arguments = bundle
                                    transaction.replace(R.id.fragment_container_carousel, fragment)
                                    transaction.commit()
                                }
                            }
                        },
                        onHelpInfoTabClick = { it, pos, view ->
                            val fragment = HelpInfoFragment {
                                handleBackClick()
                            }
                            helpInfoTabIndex = pos
                            binding.gsDown.visibility = View.GONE
                            binding.gsUp.visibility = View.GONE
                            binding.fragmentContainerHelpInfo.toVisible()
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container_help_info, fragment)
                                .commit()
                            binding.fragmentContainerCarousel.toInvisible()
                            binding.rvHotelInfoHeader.toInvisible()
                            binding.tvHeaderTitle.toInvisible()
                            binding.btnBack.toInvisible()
                            binding.layoutHeader.tvTitle.text = Constants.HELP_INFO
                            binding.tvHeaderTitle.text = ""
                        },
                        onFocusChangeListener = this
                    )
                    binding.rvHotelInfoHeader.adapter = adapter
                    binding.tvHeaderTitle.text = tabs[0]
                    binding.pbLoader.toInvisible()
                }
            }

            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { hotelInfoViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                handleBackClick()
            }

            KeyEvent.KEYCODE_ESCAPE -> {
                handleBackClick()
            }
        }
        return false
    }

    private fun handleBackClick() {
        if (binding.fragmentContainerHelpInfo.isVisible) {
            binding.fragmentContainerHelpInfo.toInvisible()
            binding.rvHotelInfoHeader.toVisible()
            binding.rvHotelInfoHeader.findViewHolderForAdapterPosition(helpInfoTabIndex)?.itemView?.requestFocus()
            binding.fragmentContainerCarousel.toVisible()
            binding.tvHeaderTitle.toVisible()
            binding.btnBack.toVisible()
            binding.layoutHeader.tvTitle.text = Constants.HOTEL_INFORMATION
            activityStack.add(this::class.java.simpleName)
        } else {
            finish()
        }
    }

    override fun onItemFocused(position: Int, itemList: List<String>) {
        if (position == 0) {
            binding.gsUp.visibility = View.GONE
        } else {
            binding.gsUp.visibility = View.VISIBLE
        }
        if (position == itemList.size - 1) {
            binding.gsDown.visibility = View.GONE
        } else {
            binding.gsDown.visibility = View.VISIBLE
        }
    }

}

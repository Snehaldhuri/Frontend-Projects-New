package com.diipl.moviebeam.ui.hotelinfo


import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.HotelInfoBtnModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.hotelservice.TabListObj
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.databinding.ActivityHotelInfoBinding
import com.diipl.moviebeam.databinding.ActivityMainMenuBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuViewModel
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar


class HotelInfoActivity : BaseActivity() {
    private val hotelInfoViewModel: HotelInfoViewModel by viewModels()
    private lateinit var binding: ActivityHotelInfoBinding

    override fun observeViewModel() {
        observe(hotelInfoViewModel.hotelServiceLiveData, ::handleHotelServiceResponse)
        observe(hotelInfoViewModel.themeLiveData, ::handleThemeResponse)
        observeSnackBarMessages(hotelInfoViewModel.showSnackBar)
        observeToast(hotelInfoViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityHotelInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                Glide.with(this).load(hotelInfoViewModel.themeLiveData.value?.data?.themeLogoFileName).into(binding.header.imgHotelLogo)
                loadBg(hotelInfoViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                binding.loaderView.toInvisible()
            }
            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleHotelServiceResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                val tabMap = mutableMapOf<String, TabListObj>()
                val tabs = mutableListOf<String>()
                val response = hotelInfoViewModel.hotelServiceLiveData.value?.data
                for (service in response?.servicesList!!){
                    when(service.categoryName){
                        "All" -> {
                            service.serviceList.forEach {
                                tabMap[it.title] = TabListObj(2, it, null)
                                tabs.add(it.title)
                            }
                        }
                        else -> {
                            tabMap[service.categoryName] = TabListObj(1, null, service)
                            tabs.add(service.categoryName)
                        }
                    }
                }

                val adapter = HotelInfoTabAdapter(tabs){
                    when(tabMap[it]?.serviceType){
                        1 -> {

                        }
                        else -> {

                        }
                    }
                    /*when(it.title){
                        "Business center"->{
                            val mBundle = Bundle()
                            mBundle.putString("title",it.title)
                            val list = CarouselListFragment()
                            val transaction = supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container_carousel, list)
                            transaction.commit()
                        }
                        "Mini Bar"->{
                            val mBundle = Bundle()
                            mBundle.putString("title",it.title)
                            val mFragment = LoginFragment()
                            val transaction =supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container_carousel, mFragment)
                            transaction.commit()
                        }
                        "Meetings"->{
                            val mBundle = Bundle()
                            mBundle.putString("title",it.title)
                            val list = CarouselListFragment()
                            val transaction = supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container_carousel, list)
                            transaction.commit()
                        }
                        "In room Dining"->{
                            val mBundle = Bundle()
                            mBundle.putString("title",it.title)
                            val mFragment = LoginFragment()// Assuming 'tab' is a TextView in your holder
                            val transaction =supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container_carousel, mFragment)
                            transaction.commit()
                        }
                        "Home"->{
                            val mBundle = Bundle()
                            mBundle.putString("title",it.title)
                            val mFragment = LoginFragment()
                            val transaction =supportFragmentManager.beginTransaction()
                            mFragment.arguments=mBundle
                            transaction.replace(R.id.fragment_container_carousel, mFragment)
                            transaction.commit()
                        }
                        "Help & Info"->{
                            val mBundle = Bundle()
                            mBundle.putString("title",it.title)
                            val mFragment = LoginFragment()// Assuming 'tab' is a TextView in your holder
                            val transaction =supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container_carousel, mFragment)
                            transaction.commit()
                        }
                        "Hotel Info"->{
                            val mBundle = Bundle()
                            mBundle.putString("title",it.title)
                            val mFragment = LoginFragment()
                            val transaction =supportFragmentManager.beginTransaction()
                            mFragment.arguments=mBundle
                            transaction.replace(R.id.fragment_container_carousel, mFragment)
                            transaction.commit()
                        }

                    }*/
                }
                binding.recyclerView.adapter = adapter


//                Log.i("Filterr", tab.toString())
//                Log.i("Success", hotelInfoViewModel.hotelServiceLiveData.value?.data?.toString()?:"")
                binding.loaderView.toInvisible()
            }
            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun getBtnList(): List<HotelInfoBtnModel>{
        val list = mutableListOf(
            HotelInfoBtnModel("Business center"),
            HotelInfoBtnModel( "Mini Bar"),
            HotelInfoBtnModel( "Meetings"),
            HotelInfoBtnModel( "In room Dining"),
            HotelInfoBtnModel("Home"),
            HotelInfoBtnModel( "Help & Info"),
            HotelInfoBtnModel( "Hotel Info"),
            HotelInfoBtnModel( "Crackle"),
            HotelInfoBtnModel( "2nd crackle")
        )
        return list
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun loadBg(imgUrl: String?){
        Glide.with(this).load(imgUrl)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    binding.root.background = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

}
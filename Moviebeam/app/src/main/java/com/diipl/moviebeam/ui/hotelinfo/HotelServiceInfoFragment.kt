package com.diipl.moviebeam.ui.hotelinfo

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentHotelServiceInfoBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.loadImagesWithGlideExtHS
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

private const val TAG = "HotelServiceInfoFragment"
class HotelServiceInfoFragment : Fragment() {
    private var title: String = ""
    private var description: String = ""
    private var serviceImgUrl: String = ""
    private var serviceImageList: List<String>? = null

    private var _binding: FragmentHotelServiceInfoBinding? = null
    val binding get() = _binding!!

    lateinit var activity: HotelInfoActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title").toString()
            description = it.getString("desc").toString()
            serviceImgUrl = it.getString("imgUrl").toString()
            serviceImageList = it.getStringArrayList(Constants.SERVICE_IMAGE_LIST_PARAM)
        }



    }

    override fun onResume() {
        super.onResume()

        requireActivity().onBackPressedDispatcher.addCallback {
            Log.e(TAG, "onResume: ")
//            onLeftKeyPressed("null")
            activity.handleBackClick()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHotelServiceInfoBinding.inflate(inflater, container, false)

        activity = requireActivity() as HotelInfoActivity

        if (description == "null" || description.isEmpty()) {
            binding.tvServiceDesc.toInvisible()
            binding.glVertical50.setGuidelinePercent(0f)
            val layoutParams = binding.ivServiceImg.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.dimensionRatio = "H,1:1.64"
            binding.ivServiceImg.layoutParams = layoutParams
        } else {
            binding.tvServiceDesc.text = description.replace("<br/>", "", true)

            binding.ivServiceImg.toGone()
            binding.viewPager.toVisible()

            val viewPager: ViewPager = binding.viewPager
            val adapter = ViewPagerAdapter(serviceImageList, viewPager)
            viewPager.adapter = adapter

            adapter.startAutoSlide()
        }
        if (serviceImgUrl != "null") {
            binding.ivServiceImg.loadImagesWithGlideExtHS(serviceImgUrl)

        }
        if(title == "Help & Info") {
            binding.ivServiceImg.toVisible()
            binding.viewPager.toGone()
            binding.ivServiceImg.setImageDrawable(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.help
                )
            })
        }
        else{
            binding.ivServiceImg.setBackgroundResource(R.drawable.hs_default)
        }



        return binding.root
    }

}
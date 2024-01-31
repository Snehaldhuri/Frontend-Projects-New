package com.diipl.moviebeam.ui.hotelinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentHotelServiceInfoBinding
import com.diipl.moviebeam.utils.loadImagesWithGlideExtHS
import com.diipl.moviebeam.utils.toInvisible

class HotelServiceInfoFragment : Fragment() {
    private var title: String = ""
    private var description: String = ""
    private var serviceImgUrl: String = ""

    private var _binding: FragmentHotelServiceInfoBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title").toString()
            description = it.getString("desc").toString()
            serviceImgUrl = it.getString("imgUrl").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHotelServiceInfoBinding.inflate(inflater, container, false)
        if (description == "null" || description.isEmpty()) {
            binding.tvServiceDesc.toInvisible()
            binding.glVertical50.setGuidelinePercent(0f)
            val layoutParams = binding.ivServiceImg.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.dimensionRatio = "H,1:1.64"
            binding.ivServiceImg.layoutParams = layoutParams
        } else {
            binding.tvServiceDesc.text = description.replace("<br/>", "", true)
            if(title == "Restaurants"){
                binding.tvServiceDesc.setText(description + "\n\nPlease scan QR Code to begin your Dining Experience.")
            }
        }
        if (serviceImgUrl != "null") {
            binding.ivServiceImg.loadImagesWithGlideExtHS(serviceImgUrl)
        }else{
            binding.ivServiceImg.setBackgroundResource(R.drawable.help)
        }



        return binding.root
    }

}
package com.diipl.moviebeam.ui.hotelinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentHotelServiceInfoBinding
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
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
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHotelServiceInfoBinding.inflate(inflater, container, false)
        if(description == "null"){
            binding.tvServiceDesc.toInvisible()
            binding.glVertical50.setGuidelinePercent(0f)
            val layoutParams = binding.ivServiceImg.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.dimensionRatio = "H,1:1.64"
            binding.ivServiceImg.layoutParams = layoutParams
        }else{
            binding.tvServiceDesc.text = description
        }
        binding.ivServiceImg.loadImagesWithGlideExt(serviceImgUrl)
        return binding.root
    }

}
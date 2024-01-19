package com.diipl.moviebeam.ui.guestservice.concierge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentVelvetParkingBinding


class VelvetParkingFragment : Fragment() {


    private var _binding: FragmentVelvetParkingBinding? = null
    val binding get() = _binding!!
    lateinit var layout_velvet_parking_number: LinearLayout
    lateinit var layout_confirmation: LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentVelvetParkingBinding.inflate(inflater, container, false)
        layout_velvet_parking_number = binding.root.findViewById(R.id.layout_velvet_parking_number)
        layout_confirmation = binding.root.findViewById(R.id.layout_confirmation)



        binding.btnOk.setOnClickListener(View.OnClickListener {
            layout_velvet_parking_number.visibility = View.GONE
            layout_confirmation.visibility = View.VISIBLE

            binding.tvMessage.text =
                "Thank you.Your request has been sent .Please proceed with valet desk to retrive your vehicle"

        })

        return binding.root
    }
}
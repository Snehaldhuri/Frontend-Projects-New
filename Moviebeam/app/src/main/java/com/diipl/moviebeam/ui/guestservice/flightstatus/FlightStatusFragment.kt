package com.diipl.moviebeam.ui.guestservice.flightstatus

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.flightstatus.FlightStatusResponse
import com.diipl.moviebeam.databinding.FragmentFlightStatusBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FlightStatusFragment(
    private val onLeftKeyPressed: () -> Unit
) : BaseFragment(), AdapterView.OnItemSelectedListener {

    private val flightStatusViewModel: FlightStatusViewModel by activityViewModels()
    private var _binding: FragmentFlightStatusBinding? = null
    val binding get() = _binding!!

    private var airPorts: List<String> = listOf()
    private var isDep = true
    private var callType = Constants.DEPARTURE
    private var apCode = ""

    private var gradientButton: GradientDrawable? = null
    private var gradientTable: GradientDrawable? = null

    override fun observeViewModel() {
        observe(flightStatusViewModel.flightStatusLiveData, ::handleFlightStatusResponse)
    }

    override fun initViewBinding() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlightStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnArrDep.setOnClickListener {
            if (isDep) {
                callType = Constants.ARRIVAL
                isDep = false
                binding.btnArrDep.text = getString(R.string.switch_to_departure)
            } else {
                callType = Constants.DEPARTURE
                isDep = true
                binding.btnArrDep.text = getString(R.string.switch_to_arrival)
            }
            fetchFlightStatus()
        }

        binding.btnArrDep.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.background = gradientButton
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }

        val dropdown: Spinner = binding.spAirport

        val adapter = ArrayAdapter(binding.root.context, R.layout.item_spinner_header, airPorts)
        adapter.setDropDownViewResource(R.layout.item_spinner_item)

        dropdown.adapter = adapter
        dropdown.onItemSelectedListener = this
        dropdown.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.background = gradientButton
                view.findViewById<TextView>(R.id.tv_title)?.let {
                    it.isSelected = true
                }
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                view.findViewById<TextView>(R.id.tv_title)?.let {
                    it.isSelected = false
                }
            }
        }
        dropdown.requestFocus()
        dropdown.setOnKeyListener { _, keycode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keycode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> onLeftKeyPressed()
                }
            }
            false
        }
    }

    private fun handleFlightStatusResponse(status: Resource<FlightStatusResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                flightStatusViewModel.flightStatusLiveData.value?.data?.flightList?.let {
                    binding.layoutFlightStatusTable.rvTableContent.layoutManager =
                        LinearLayoutManager(this.context)

                    val tableAdapter =
                        FlightStatusTableAdapter(
                            onFlightFocused = ::handleFlightStatusFocus,
                            onLeftKeyPressed = onLeftKeyPressed
                        )
                    tableAdapter.setFlightList(it)
                    binding.layoutFlightStatusTable.rvTableContent.adapter = tableAdapter
                }
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { flightStatusViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    fun setAirportList(airPorts: String) {
        this.airPorts = airPorts.split(",")
    }

    private fun fetchFlightStatus() {
        flightStatusViewModel.getFlightStatus(
            Constants.FLIGHT_STATUS_CMD,
            Constants.UA,
            callType,
            apCode,
            Constants.MODE
        )
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item: String = parent?.getItemAtPosition(position).toString()
        apCode = item.substring(0, item.indexOf(")") + 1)
        fetchFlightStatus()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        apCode = ""
        fetchFlightStatus()
    }

    private fun handleFlightStatusFocus(view: View, isFocused: Boolean) {
        if (isFocused) {
            view.background = gradientTable
        } else {
            view.setBackgroundResource(R.color.transparent)
        }
    }

    fun setGradientColor(startColor: String, endColor: String) {
        gradientButton = getGradient(startColor, endColor)
        gradientTable = getGradient(startColor, endColor, true)
    }

    private fun getGradient(
        startColor: String,
        endColor: String,
        isTable: Boolean = false
    ): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )
        if (!isTable)
            gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

}
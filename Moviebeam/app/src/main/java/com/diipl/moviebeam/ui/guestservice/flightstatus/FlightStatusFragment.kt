package com.diipl.moviebeam.ui.guestservice.flightstatus

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
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.flightstatus.FlightStatusResponse
import com.diipl.moviebeam.databinding.FragmentFlightStatusBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.getGradientColorForTable
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FlightStatusFragment(
    private val onLeftKeyPressed: () -> Unit,
    private val flightStatusChangedListener: OnFlightStatusChangedListener? = null
) : BaseFragment(), AdapterView.OnItemSelectedListener {

    private val flightStatusViewModel: FlightStatusViewModel by activityViewModels()
    private var _binding: FragmentFlightStatusBinding? = null
    private val binding get() = _binding!!

    private var airPorts: List<String> = listOf()
    private var isDep = true
    private var callType = Constants.DEPARTURE
    private var apCode = ""

    interface OnFlightStatusChangedListener {
        fun onFlightStatusChanged(isDeparture: Boolean)
    }

    override fun observeViewModel() {
        observe(flightStatusViewModel.flightStatusLiveData, ::handleFlightStatusResponse)
    }

    override fun initViewBinding() {}

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
            flightStatusChangedListener?.onFlightStatusChanged(isDep)
        }

        binding.btnArrDep.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.background = getGradientColor()
                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                view.nextFocusUpId = View.NO_ID
                                return@setOnKeyListener true
                            }
                        }
                    }
                    false
                }
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }

        val dropdown: Spinner = binding.spAirport

        val adapter = ArrayAdapter(
            binding.root.context,
            R.layout.item_spinner_header,
            airPorts
        )
        adapter.setDropDownViewResource(R.layout.item_spinner_item)

        dropdown.adapter = adapter
        dropdown.onItemSelectedListener = this
        dropdown.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.background = getGradientColor()
                view.findViewById<TextView>(R.id.tv_title)?.let {
                    it.isSelected = true
                }
                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                view.nextFocusUpId = View.NO_ID
                                return@setOnKeyListener true
                            }

                            KeyEvent.KEYCODE_DPAD_LEFT -> onLeftKeyPressed()
                        }
                    }
                    false
                }
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                view.findViewById<TextView>(R.id.tv_title)?.let {
                    it.isSelected = false
                }
            }
        }
        dropdown.requestFocus()
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
            view.background = getGradientColorForTable()
        } else {
            view.setBackgroundResource(R.color.transparent)
        }
    }

}

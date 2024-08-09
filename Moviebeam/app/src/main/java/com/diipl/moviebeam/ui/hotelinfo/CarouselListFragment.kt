package com.diipl.moviebeam.ui.hotelinfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import com.diipl.moviebeam.data.dto.hotelservice.Service

private const val TAG = "CarouselListFragment"
class CarouselListFragment(
    private val onItemFocused: (String) -> Unit,
    private val onLeftKeyPressed: (String) -> Unit
) : RowsSupportFragment() {

    private var serviceList: List<Service>? = null
    lateinit var activity: HotelInfoActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRows()
        activity = requireActivity() as HotelInfoActivity
    }

    override fun onResume() {
        super.onResume()

        requireActivity().onBackPressedDispatcher.addCallback {
            Log.e(TAG, "onResume: ")
            onLeftKeyPressed("null")
            activity.handleBackClick()
        }

    }

    private fun setupRows() {
        // Create Rows and Cards
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter(FocusHighlight.ZOOM_FACTOR_XSMALL))

        // Example Row 1
        val row1Adapter = createCardRow()
        rowsAdapter.add(ListRow(row1Adapter))

        // Set the adapter
        adapter = rowsAdapter
    }

    private fun createCardRow(): ArrayObjectAdapter {
        val presenter = MyCardPresenter(onItemFocused, onLeftKeyPressed)
        val adapter = ArrayObjectAdapter(presenter)

        // Add cards to the row
        serviceList?.forEach { adapter.add(it) }
        serviceList?.let {
            presenter.rowLength = it.size
        }

        return adapter
    }

    fun bindData(serviceList: List<Service>?) {
        this.serviceList = serviceList
    }
}
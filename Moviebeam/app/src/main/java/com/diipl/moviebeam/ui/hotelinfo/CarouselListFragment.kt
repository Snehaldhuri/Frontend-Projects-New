package com.diipl.moviebeam.ui.hotelinfo

import android.os.Bundle
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import com.diipl.moviebeam.data.dto.hotelservice.Service


class CarouselListFragment(private val onItemFocused: ((String)) -> Unit) : RowsSupportFragment() {

    private var serviceList: List<Service>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRows()
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
        val adapter = ArrayObjectAdapter(MyCardPresenter(onItemFocused))

        // Add cards to the row
        serviceList?.forEach { adapter.add(it) }
        return adapter
    }

    fun bindData(serviceList: List<Service>?) {
        this.serviceList = serviceList
    }
}
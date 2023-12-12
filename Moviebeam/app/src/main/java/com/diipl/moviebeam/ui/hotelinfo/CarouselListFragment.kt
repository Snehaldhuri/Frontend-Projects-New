package com.diipl.moviebeam.ui.hotelinfo

import android.os.Bundle
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter


class CarouselListFragment: RowsSupportFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRows()
    }

    private fun setupRows() {
        // Create Rows and Cards
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter(FocusHighlight.ZOOM_FACTOR_XSMALL))

        // Example Row 1
        val header1 = HeaderItem(0, "Row 1")
        val row1Adapter = createCardRow()
        rowsAdapter.add(ListRow(header1, row1Adapter))

        // Set the adapter
        adapter = rowsAdapter
    }

    private fun createCardRow(): ArrayObjectAdapter {
        val adapter = ArrayObjectAdapter(MyCardPresenter())

        // Add cards to the row
        for (i in 0..4) {
            // Assuming CardItem is a custom data model class
            val cardItem = CardItem("Any discussion of service and amenities at The Highpoint Hotel must begin with our personal and perceptive Highpoint Attach signature service. Available to any guest of our Manhattan luxury New York hotel 24 hours a day, an Attach offers everything from personal and business assistance to custom-stocked kitchens and personalized business cards and stationary. It's the very definition of sophistication without pretension, attention without intrusion, and service without boundaries. $i")
            adapter.add(cardItem)
        }
        return adapter
    }
}
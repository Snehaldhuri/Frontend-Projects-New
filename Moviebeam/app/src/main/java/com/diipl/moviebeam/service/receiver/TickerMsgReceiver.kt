package com.diipl.moviebeam.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.datastore.core.DataStore
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.dto.ticker.TvTickerDTO
import com.diipl.moviebeam.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TickerMsgReceiver : BroadcastReceiver() {

    private var tickerDTO: TvTickerDTO? = null

    @Inject
    lateinit var tickerDatastore: DataStore<TickerResponse>

    override fun onReceive(context: Context?, intent: Intent?) {
        tickerDTO = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.extras?.getParcelable(Constants.TICKER_DTO_PARAM, TvTickerDTO::class.java)
        } else {
            intent?.extras?.getParcelable(Constants.TICKER_DTO_PARAM)
        }
        updateTickerMessage(tickerDatastore)
    }

    private fun updateTickerMessage(dataStore: DataStore<TickerResponse>) {
        CoroutineScope(Dispatchers.IO).launch {
            val tvTickerList = dataStore.data.first().tvTickerList?.toMutableList()
            tvTickerList?.remove(tickerDTO)
            updateTickerResponseData(tickerDatastore, tvTickerList)
        }
    }

    private fun updateTickerResponseData(
        dataStore: DataStore<TickerResponse>,
        tvTickerList: MutableList<TvTickerDTO>?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    tvTickerList = tvTickerList,
                )
            }
        }
    }

}
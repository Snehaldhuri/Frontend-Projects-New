package com.diipl.moviebeam.ui.refreshingui

private const val TAG = "UpdateDataWorker"

/*
class UpdateDataWorker @Inject constructor(
    private val moviesDataStore: DataStore<MoviesResponse>,
    private val showTimeDataStore: DataStore<ShowTimeResponse>,
    private val movieBeamRepository: MovieBeamRepository,
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val isMovie = inputData.getBoolean("isMovie", false)

        try {
            if (isMovie) {
                val response = movieBeamRepository.getMoviesInfo(Constants.UA)
                if (response != null) {
                    updateSyncList(moviesDataStore, response)
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    LoggingService.sendMessageToWebSocket("In MoviesReleasesCollection callbackSuccess ")
                } else {
                    LoggingService.sendMessageToWebSocket("In Movies callback fail ")
                }
            } else {
                val response = movieBeamRepository.getShowtimeInfo(Constants.UA)
                if (response != null) {
                    updateShowtimeData(showTimeDataStore, response)
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    LoggingService.sendMessageToWebSocket("In ShowtimeReleasesCollection callbackSuccess ")
                } else {
                    LoggingService.sendMessageToWebSocket("In Showtime callback fail ")
                }
            }

            return Result.success()
        } catch (e: Exception) {

            return Result.failure()
        }

    }

    private fun updateSyncList(
        dataStore: DataStore<MoviesResponse>,
        data: MoviesResponse
    ) {
        Constants.C_LIST_VERSION = data.version
        CoroutineScope(Dispatchers.IO).launch {

            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    accountId = data.accountId,
                    adultDayPassPrice = data.adultDayPassPrice,
                    id = data.id,
                    type = data.type,
                    version = data.version,
                    freeContentList = data.freeContentList,
                    freeGenreList = data.freeGenreList,
                    premiumContentList = data.premiumContentList,
                    premiumGenreList = data.premiumGenreList
                )
            }
        }
    }

    private fun updateShowtimeData(
        dataStore: DataStore<ShowTimeResponse>,
        data: ShowTimeResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    accountId = data.accountId,
                    id = data.id,
                    shoContentList = data.shoContentList,
                    shoGenreList = data.shoGenreList,
                    type = data.type,
                    version = data.version
                )
            }
        }
    }

}*/

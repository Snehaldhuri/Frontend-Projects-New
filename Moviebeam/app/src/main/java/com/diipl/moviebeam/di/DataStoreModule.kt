package com.diipl.moviebeam.di

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.service.PreferenceHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

private const val THEME_RESPONSE_DATA_STORE_FILE_NAME = "theme_response_prefs.pb"
private const val ACCOUNT_SETUP_DATA_STORE_FILE_NAME = "account_setup_prefs.pb"
private const val WEATHER_DATA_STORE_FILE_NAME = "weather_new_prefs.pb"
private const val DATE_TIME_DATA_STORE_FILE_NAME = "date_time_prefs.pb"
private const val HOTEL_SERVICE_DATA_STORE_FILE_NAME = "hotel_service_prefs.pb"
private const val LOCAL_ATTRACTION_DATA_STORE_FILE_NAME = "local_attraction_prefs.pb"
private const val MOVIES__DATA_STORE_FILE_NAME = "movies_prefs.pb"
private const val SHOWTIME__DATA_STORE_FILE_NAME = "showtime_prefs.pb"
private const val GUEST_DETAILS_DATA_STORE_FILE_NAME = "guests_details.pb"
private const val CHANNEL_LIST_DATA_STORE_FILE_NAME = "channel_list.pb"
private const val TICKER_DATA_STORE_FILE_NAME = "ticker_prefs.pb"
private const val MESSAGE_DATA_STORE_FILE_NAME = "message_prefs.pb"

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

    @Singleton
    @Provides
    fun providePreferenceHandler(@ApplicationContext appContext: Context): PreferenceHandler {
        return PreferenceHandler(appContext)
    }

    @Singleton
    @Provides
    fun provideThemeResponseDataStore(@ApplicationContext appContext: Context): DataStore<ThemeResponse> {
        return DataStoreFactory.create(
            serializer = ThemeResponseSerializer(),
            produceFile = { appContext.dataStoreFile(THEME_RESPONSE_DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { ThemeResponse() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideAccountSetupDataStore(@ApplicationContext appContext: Context): DataStore<AccountSetupResponse> {
        return DataStoreFactory.create(
            serializer = AccountSetupSerializer(),
            produceFile = { appContext.dataStoreFile(ACCOUNT_SETUP_DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { AccountSetupResponse() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideWeatherDataStore(@ApplicationContext appContext: Context): DataStore<WeatherResponse> {
        return DataStoreFactory.create(
            serializer = WeatherSerializer(),
            produceFile = { appContext.dataStoreFile(WEATHER_DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { WeatherResponse() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideDateTimeDataStore(@ApplicationContext appContext: Context): DataStore<DateTimeResponse> {
        return DataStoreFactory.create(
            serializer = DateTimeSerializer(),
            produceFile = { appContext.dataStoreFile(DATE_TIME_DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { DateTimeResponse() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideHotelServiceDataStore(@ApplicationContext appContext: Context): DataStore<HotelServiceResponse> {
        return DataStoreFactory.create(
            serializer = HotelServiceSerializer(),
            produceFile = { appContext.dataStoreFile(HOTEL_SERVICE_DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { HotelServiceResponse() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideLocalAttractionDataStore(@ApplicationContext appContext: Context): DataStore<LocalAttractionResponse> {
        return DataStoreFactory.create(
            serializer = LocalAttractionSerializer(),
            produceFile = { appContext.dataStoreFile(LOCAL_ATTRACTION_DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { LocalAttractionResponse() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideMoviesDataStore(@ApplicationContext appContext: Context): DataStore<MoviesResponse> {
        return DataStoreFactory.create(
            serializer = MoviesSerializer(),
            produceFile = { appContext.dataStoreFile(MOVIES__DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { MoviesResponse() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideShowTimeDataStore(@ApplicationContext appContext: Context): DataStore<ShowTimeResponse> {
        return DataStoreFactory.create(
            serializer = ShowTimeSerializer(),
            produceFile = { appContext.dataStoreFile(SHOWTIME__DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { ShowTimeResponse() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideGuestDetailsDataStore(@ApplicationContext appContext: Context): DataStore<CmdDataDto> {
        return DataStoreFactory.create(
            serializer = GuestDetailsSerializer(),
            produceFile = { appContext.dataStoreFile(GUEST_DETAILS_DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { CmdDataDto() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideChannelListDataStore(@ApplicationContext appContext: Context): DataStore<ChannelListResponse> {
        return DataStoreFactory.create(
            serializer = ChannelListSerializer(),
            produceFile = { appContext.dataStoreFile(CHANNEL_LIST_DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { ChannelListResponse() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideTickerDataStore(@ApplicationContext appContext: Context): DataStore<TickerResponse> {
        return DataStoreFactory.create(
            serializer = TickerSerializer(),
            produceFile = { appContext.dataStoreFile(TICKER_DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { TickerResponse() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideMessageDataStore(@ApplicationContext appContext: Context): DataStore<MessageResponse> {
        return DataStoreFactory.create(
            serializer = GuestMessageSerializer(),
            produceFile = { appContext.dataStoreFile(MESSAGE_DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { MessageResponse() }
            ),
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

}

@Singleton
class ThemeResponseSerializer @Inject constructor() :
    Serializer<ThemeResponse> {
    override val defaultValue: ThemeResponse
        get() = ThemeResponse()

    override suspend fun readFrom(input: InputStream): ThemeResponse =
        try {
            Json.decodeFromString(ThemeResponse.serializer(), input.readBytes().decodeToString())
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: ThemeResponse, output: OutputStream) {
        output.write(
            Json.encodeToString(ThemeResponse.serializer(), t)
                .encodeToByteArray()
        )
    }
}

@Singleton
class AccountSetupSerializer @Inject constructor() : Serializer<AccountSetupResponse> {
    override val defaultValue: AccountSetupResponse
        get() = AccountSetupResponse()

    override suspend fun readFrom(input: InputStream): AccountSetupResponse =
        try {
            Json.decodeFromString(
                AccountSetupResponse.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: AccountSetupResponse, output: OutputStream) {
        output.write(
            Json.encodeToString(AccountSetupResponse.serializer(), t)
                .encodeToByteArray()
        )
    }
}

@Singleton
class WeatherSerializer @Inject constructor() : Serializer<WeatherResponse> {
    override val defaultValue: WeatherResponse
        get() = WeatherResponse()

    override suspend fun readFrom(input: InputStream): WeatherResponse =
        try {
            Json.decodeFromString(WeatherResponse.serializer(), input.readBytes().decodeToString())
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: WeatherResponse, output: OutputStream) {
        output.write(
            Json.encodeToString(WeatherResponse.serializer(), t)
                .encodeToByteArray()
        )
    }
}

@Singleton
class DateTimeSerializer @Inject constructor() : Serializer<DateTimeResponse> {
    override val defaultValue: DateTimeResponse
        get() = DateTimeResponse()

    override suspend fun readFrom(input: InputStream): DateTimeResponse =
        try {
            Json.decodeFromString(DateTimeResponse.serializer(), input.readBytes().decodeToString())
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: DateTimeResponse, output: OutputStream) {
        output.write(
            Json.encodeToString(DateTimeResponse.serializer(), t)
                .encodeToByteArray()
        )
    }
}

@Singleton
class HotelServiceSerializer @Inject constructor() : Serializer<HotelServiceResponse> {
    override val defaultValue: HotelServiceResponse
        get() = HotelServiceResponse()

    override suspend fun readFrom(input: InputStream): HotelServiceResponse =
        try {
            Json.decodeFromString(
                HotelServiceResponse.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: HotelServiceResponse, output: OutputStream) {
        output.write(
            Json.encodeToString(HotelServiceResponse.serializer(), t)
                .encodeToByteArray()
        )
    }
}

@Singleton
class LocalAttractionSerializer @Inject constructor() : Serializer<LocalAttractionResponse> {
    override val defaultValue: LocalAttractionResponse
        get() = LocalAttractionResponse()

    override suspend fun readFrom(input: InputStream): LocalAttractionResponse =
        try {
            Json.decodeFromString(
                LocalAttractionResponse.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: LocalAttractionResponse, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(LocalAttractionResponse.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}

@Singleton
class MoviesSerializer @Inject constructor() : Serializer<MoviesResponse> {
    override val defaultValue: MoviesResponse
        get() = MoviesResponse()

    override suspend fun readFrom(input: InputStream): MoviesResponse =
        try {
            Json.decodeFromString(
                MoviesResponse.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: MoviesResponse, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(MoviesResponse.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}

@Singleton
class ShowTimeSerializer @Inject constructor() : Serializer<ShowTimeResponse> {
    override val defaultValue: ShowTimeResponse
        get() = ShowTimeResponse()

    override suspend fun readFrom(input: InputStream): ShowTimeResponse =
        try {
            Json.decodeFromString(
                ShowTimeResponse.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: ShowTimeResponse, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(ShowTimeResponse.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}

@Singleton
class GuestDetailsSerializer @Inject constructor() : Serializer<CmdDataDto> {
    override val defaultValue: CmdDataDto
        get() = CmdDataDto()

    override suspend fun readFrom(input: InputStream): CmdDataDto =
        try {
            Json.decodeFromString(
                CmdDataDto.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: CmdDataDto, output: OutputStream) {
        output.write(
            Json.encodeToString(CmdDataDto.serializer(), t)
                .encodeToByteArray()
        )
    }
}

@Singleton
class ChannelListSerializer @Inject constructor() : Serializer<ChannelListResponse> {
    override val defaultValue: ChannelListResponse
        get() = ChannelListResponse()

    override suspend fun readFrom(input: InputStream): ChannelListResponse =
        try {
            Json.decodeFromString(
                ChannelListResponse.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: ChannelListResponse, output: OutputStream) {
        output.write(
            Json.encodeToString(ChannelListResponse.serializer(), t)
                .encodeToByteArray()
        )
    }
}

@Singleton
class TickerSerializer @Inject constructor() : Serializer<TickerResponse> {
    override val defaultValue: TickerResponse
        get() = TickerResponse()

    override suspend fun readFrom(input: InputStream): TickerResponse =
        try {
            Json.decodeFromString(
                TickerResponse.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: TickerResponse, output: OutputStream) {
        output.write(
            Json.encodeToString(TickerResponse.serializer(), t)
                .encodeToByteArray()
        )
    }
}

@Singleton
class GuestMessageSerializer @Inject constructor() : Serializer<MessageResponse> {
    override val defaultValue: MessageResponse
        get() = MessageResponse()

    override suspend fun readFrom(input: InputStream): MessageResponse =
        try {
            Json.decodeFromString(
                MessageResponse.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: MessageResponse, output: OutputStream) {
        output.write(
            Json.encodeToString(MessageResponse.serializer(), t)
                .encodeToByteArray()
        )
    }
}

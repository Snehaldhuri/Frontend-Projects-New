package com.diipl.moviebeam.di

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

    @Singleton
    @Provides
    fun provideThemeResponseDataStore(@ApplicationContext appContext: Context): DataStore<ThemeResponse> {
        return DataStoreFactory.create(
            serializer = ThemeResponseSerializer(),
            produceFile = { appContext.dataStoreFile(THEME_RESPONSE_DATA_STORE_FILE_NAME) },
            corruptionHandler = null,
            migrations = listOf(
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideAccountSetupDataStore(@ApplicationContext appContext: Context): DataStore<AccountSetupResponse> {
        return DataStoreFactory.create(
            serializer = AccountSetupSerializer(),
            produceFile = { appContext.dataStoreFile(ACCOUNT_SETUP_DATA_STORE_FILE_NAME) },
            corruptionHandler = null,
            migrations = listOf(
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideWeatherDataStore(@ApplicationContext appContext: Context): DataStore<WeatherResponse> {
        return DataStoreFactory.create(
            serializer = WeatherSerializer(),
            produceFile = { appContext.dataStoreFile(WEATHER_DATA_STORE_FILE_NAME) },
            corruptionHandler = null,
            migrations = listOf(
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideDateTimeDataStore(@ApplicationContext appContext: Context): DataStore<DateTimeResponse> {
        return DataStoreFactory.create(
            serializer = DateTimeSerializer(),
            produceFile = { appContext.dataStoreFile(DATE_TIME_DATA_STORE_FILE_NAME) },
            corruptionHandler = null,
            migrations = listOf(
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideHotelServiceDataStore(@ApplicationContext appContext: Context): DataStore<HotelServiceResponse> {
        return DataStoreFactory.create(
            serializer = HotelServiceSerializer(),
            produceFile = { appContext.dataStoreFile(HOTEL_SERVICE_DATA_STORE_FILE_NAME) },
            corruptionHandler = null,
            migrations = listOf(
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideLocalAttractionDataStore(@ApplicationContext appContext: Context): DataStore<LocalAttractionResponse> {
        return DataStoreFactory.create(
            serializer = LocalAttractionSerializer(),
            produceFile = { appContext.dataStoreFile(LOCAL_ATTRACTION_DATA_STORE_FILE_NAME) },
            corruptionHandler = null,
            migrations = listOf(
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideMoviesDataStore(@ApplicationContext appContext: Context): DataStore<MoviesResponse> {
        return DataStoreFactory.create(
            serializer = MoviesSerializer(),
            produceFile = { appContext.dataStoreFile(MOVIES__DATA_STORE_FILE_NAME) },
            corruptionHandler = null,
            migrations = listOf(
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideShowTimeDataStore(@ApplicationContext appContext: Context): DataStore<ShowTimeResponse> {
        return DataStoreFactory.create(
            serializer = ShowTimeSerializer(),
            produceFile = { appContext.dataStoreFile(SHOWTIME__DATA_STORE_FILE_NAME) },
            corruptionHandler = null,
            migrations = listOf(
            ),
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
        output.write(
            Json.encodeToString(LocalAttractionResponse.serializer(), t)
                .encodeToByteArray()
        )
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
        output.write(
            Json.encodeToString(MoviesResponse.serializer(), t)
                .encodeToByteArray()
        )
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
        output.write(
            Json.encodeToString(ShowTimeResponse.serializer(), t)
                .encodeToByteArray()
        )
    }
}


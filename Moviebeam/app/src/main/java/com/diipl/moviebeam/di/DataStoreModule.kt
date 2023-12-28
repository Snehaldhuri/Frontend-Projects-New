package com.diipl.moviebeam.di

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
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
            Json.decodeFromString(AccountSetupResponse.serializer(), input.readBytes().decodeToString())
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


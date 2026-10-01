package com.example.open_fashion.core.network

import com.example.open_fashion.core.storage.TokenManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Central Retrofit & OkHttp networking factory for Open Fashion Android client.
 */
object ApiClient {
    // 127.0.0.1 with `adb reverse tcp:5000 tcp:5000` enables seamless connection on emulators & physical devices
    private const val BASE_URL = "http://127.0.0.1:5000/api/v1/"

    private var tokenManager: TokenManager? = null

    /**
     * Optional initialization method to supply TokenManager for JWT bearer injection.
     */
    fun init(tokenManager: TokenManager) {
        this.tokenManager = tokenManager
    }

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)

        tokenManager?.let { tm ->
            builder.addInterceptor(AuthInterceptor(tm))
        }

        return builder.build()
    }

    val retrofit: Retrofit
        get() {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
        }

    /**
     * Inline reified helper to instantiate Retrofit API service interfaces.
     */
    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}

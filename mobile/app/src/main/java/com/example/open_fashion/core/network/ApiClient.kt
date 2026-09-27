package com.example.open_fashion.core.network

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
    /**
     * Base URL for local development:
     * - 10.0.2.2 is the special Android Emulator IP mapping to the host PC localhost.
     * - For physical device testing over Wi-Fi, change this to your PC's LAN IP (e.g. 192.168.1.X:5000).
     */
    private const val BASE_URL = "http://10.0.2.2:5000/api/v1/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    /**
     * Inline reified helper to instantiate Retrofit API service interfaces.
     * Example: val authApi = ApiClient.create<AuthApiService>()
     */
    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}

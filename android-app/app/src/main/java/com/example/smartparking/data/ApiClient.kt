package com.example.smartparking.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // 10.0.2.2 désigne le PC depuis l'émulateur Android
    private const val BASE_URL = "http://localhost:9090/"

    // Branchés par MainActivity
    var tokenProvider: () -> String? = { null }
    var onUnauthorized: () -> Unit = {}

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val path = original.url.encodedPath
        val isPublicAuthRoute = path == "/api/auth/login" || path == "/api/auth/register"

        val token = if (isPublicAuthRoute) null else tokenProvider()
        val request = if (token != null) {
            original.newBuilder().header("Authorization", "Bearer $token").build()
        } else original

        val response = chain.proceed(request)
        if (response.code == 401 && token != null) onUnauthorized()
        response
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        redactHeader("Authorization")
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}
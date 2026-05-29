package com.example.pierwszaaplikacja.network

import com.example.pierwszaaplikacja.model.Trail
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.getValue

interface ApiService {
    @GET("trails")
    suspend fun getTrails(): List<Trail>

    @GET("trail/{trailId}")
    suspend fun getTrail(@Path("trailId") trailId: Int): Trail
}

object RetrofitClient {
    const val BASE_URL = "https://2g4f.web.svpj.pl/"

    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

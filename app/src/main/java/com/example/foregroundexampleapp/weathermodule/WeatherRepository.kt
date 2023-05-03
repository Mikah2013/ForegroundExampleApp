package com.example.foregroundexampleapp.weathermodule

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class WeatherRepository {

    private val weatherApi: WeatherApi

    init {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(WeatherDataInterceptor())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
        weatherApi = retrofit.create()
    }

    suspend fun fetchWeatherData(latitude: String, longitude: String): WeatherDataResponse {
        return weatherApi.fetchContents(latitude,longitude)
    }
}
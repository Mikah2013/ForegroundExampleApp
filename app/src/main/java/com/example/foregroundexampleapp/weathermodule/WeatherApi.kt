package com.example.foregroundexampleapp.weathermodule

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    /*@GET("weather")
    suspend fun fetchContents(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String
    ): WeatherDataResponse*/

    /*@GET(
        "weather?"
                + "&lat=$longitude"
                + "&lon=-2.15"
    )*/
    @GET("weather")
    suspend fun fetchContents(@Query("lat") latitude: String, @Query("lon") longitude: String): WeatherDataResponse


}
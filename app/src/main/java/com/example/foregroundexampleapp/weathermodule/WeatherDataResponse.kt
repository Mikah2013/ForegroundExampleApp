package com.example.foregroundexampleapp.weathermodule

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherDataResponse(
    //@Json(name = "weather") val weatherData: WeatherData
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

@JsonClass(generateAdapter = true)
data class Coord(
    val lon: Double,
    val lat: Double
)

@JsonClass(generateAdapter = true)
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@JsonClass(generateAdapter = true)
data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int,
    val grnd_level: Int
)

@JsonClass(generateAdapter = true)
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

@JsonClass(generateAdapter = true)
data class Clouds(
    val all: Int
)

@JsonClass(generateAdapter = true)
data class Sys(
    val country: String?=null,
    val sunrise: Long,
    val sunset: Long
)
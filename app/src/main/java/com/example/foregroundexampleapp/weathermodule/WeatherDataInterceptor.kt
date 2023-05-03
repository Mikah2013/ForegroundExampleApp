package com.example.foregroundexampleapp.weathermodule

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val TAG = "Weather Data Interceptor"

private const val API_KEY = "83ba542ebbafa8c64c88e88db62883a6"

class WeatherDataInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val newUrl: HttpUrl = originalRequest.url.newBuilder()
            .addQueryParameter("appid", API_KEY)
            .build()
        Log.d(TAG, "Response received - New Request: $newUrl")
        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)

    }
}
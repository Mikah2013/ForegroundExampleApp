package com.example.foregroundexampleapp.weathermodule

import android.content.Context
import android.util.Base64
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import com.example.foregroundexampleapp.R
import java.text.SimpleDateFormat
import java.util.*

class WeatherUtilities {

    fun kelvinToCelsius(kelvin: Double): String {
        val celsius = kelvin - 273.15
        // This function returns celsius in double, but then
        // we convert the double to an integer to round off to no decimal points
        // and then convert into a string
        return celsius.toInt().toString() + "°C"
    }

    fun formatDate(timeZoneOffsetSeconds: Int): String {
        val timeZone =
            TimeZone.getTimeZone("GMT").also { it.rawOffset = timeZoneOffsetSeconds * 1000 }
        // Create a SimpleDateFormat object with the desired format
        val dateFormat = SimpleDateFormat("dd MMMM, hh:mm a")
        // Set the time zone for the date format
        dateFormat.timeZone = timeZone
        // Get the current date and time in the desired time zone
        val currentTime = Date()
        // Format the date and time using the date format
        return dateFormat.format(currentTime)
    }



    fun getWeatherType(listOrArray: List<Weather>): String? {

        val output = listOrArray.map { it.description }.joinToString(separator = " ")
        return output.replaceFirst(output[0], output[0].uppercaseChar())
    }


    fun placeWeatherIcon(listOrArray: List<Weather>): String? {
        val iconCode = listOrArray.map { it.icon }.joinToString(separator = " ")

        return "https://openweathermap.org/img/w/$iconCode.png"
    }

    /*fun placeWeatherIcon(listOrArray: List<Weather>): Bitmap? {
        val iconCode = listOrArray.map { it.icon }.joinToString(separator = " ")
        val mLink = "http://openweathermap.org/img/w/$iconCode.png"
        val imageURL = mLink[0]
        var image: Bitmap? = null
        try {
            val `in` = java.net.URL(imageURL.toString()).openStream()
            image = BitmapFactory.decodeStream(`in`)
        }
        catch (e: Exception) {
            Log.e("Error Message, No Image link found", e.message.toString())
            e.printStackTrace()
        }
        return image
    }*/

    /*fun placeWeatherIcon(listOrArray: List<Weather>): Bitmap? {
        val iconCode = listOrArray.map { it.icon }.joinToString(separator = " ")
        val mLink = "http://openweathermap.org/img/w/$iconCode.png"
        val decodedString: ByteArray = Base64.decode(mLink, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
      return decodedBitmap
    }*/


    fun formatTime(time: Long): String? {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(time * 1000))
    }


    fun formatHumidity(humidity: Int): String? {
        return "$humidity%"
    }

    fun formatPressure(pressure: Int): String? {
        return  "$pressure" + "hPa"
    }

    fun formatWind(wind: Double): String? {
        return "$wind" + "m/s"
    }

    fun saveLocation(applicationContext: Context?, lastLocation: Location?) {
        val locationPreferences = applicationContext?.getSharedPreferences(R.string.my_location.toString(), Context.MODE_PRIVATE)
        val editor = locationPreferences?.edit()
        editor?.putFloat("lat", lastLocation!!.latitude.toFloat())
        editor?.putFloat("lon", lastLocation!!.longitude.toFloat())
        editor?.apply()
        if (editor != null) {
            val lat = lastLocation!!.latitude
            val lon = lastLocation!!.longitude
            Log.d("WeatherUtilities", "Preference value saved $lat : $lon")
        } else {
            Log.d("WeatherUtilities", "No Coordinates have been saved")
        }
    }


}
package com.example.foregroundexampleapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.foregroundexampleapp.R
import com.example.foregroundexampleapp.databinding.FragmentWeatherBinding
import com.example.foregroundexampleapp.weathermodule.WeatherRepository
import com.example.foregroundexampleapp.weathermodule.WeatherUtilities
import com.google.android.gms.location.*
import kotlinx.coroutines.launch


private const val TAG = "Weather Fragment"

@Suppress("DEPRECATION")
class WeatherFragment : Fragment() {

    private lateinit var locationPreferences: SharedPreferences
    private lateinit var binding: FragmentWeatherBinding


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.d(TAG, "onCreate")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWeatherBinding.inflate(layoutInflater, container, false)
        Log.d(TAG, "Weather Fragment View Created")
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        locationPreferences = requireContext().getSharedPreferences(R.string.my_location.toString(), Context.MODE_PRIVATE)
        val latitude = locationPreferences.getFloat("lat", 0f).toDouble()
        val longitude = locationPreferences.getFloat("lon", 0f).toDouble()
        runTheApp(latitude, longitude)
        binding.refreshButton.setOnClickListener {
            //runTheApp()
            binding.noInternetLayout.visibility = View.GONE
        }

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_weather_menu, menu)
    }

    private fun runTheApp(mLatitude: Double, mLongitude: Double) {
        //getLocation()
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {

                //val response = WeatherRepository().fetchContents()
                val response = WeatherRepository().fetchWeatherData(mLatitude.toString(), mLongitude.toString())
                Log.d(TAG, "Response received: $response")
                //Log.d(TAG, "My Location is ## : ${latitude}, ${longitude}")
                val weatherUtilities = WeatherUtilities()
                val mainTempText = weatherUtilities.kelvinToCelsius(response.main.temp)
                val feelsLikeTempText = "Feels Like " + weatherUtilities.kelvinToCelsius(response.main.feels_like)
                val maximumTempText = "High " + weatherUtilities.kelvinToCelsius(response.main.temp_max)
                val minimumTempText = "Low " + weatherUtilities.kelvinToCelsius(response.main.temp_min)
                val weatherTypeText = weatherUtilities.getWeatherType(response.weather)
                Log.d(TAG, "Weather Type is: $weatherTypeText")
                val dateText = weatherUtilities.formatDate(response.timezone)
                val weatherIcon = weatherUtilities.placeWeatherIcon(response.weather)
                val sunriseText = weatherUtilities.formatTime(response.sys.sunrise)
                val sunSetText = weatherUtilities.formatTime(response.sys.sunset)
                val humidityText = weatherUtilities.formatHumidity(response.main.humidity)
                val pressureText = weatherUtilities.formatPressure(response.main.pressure)
                val windSpeedText = weatherUtilities.formatWind(response.wind.speed)
                val mLocationText = response.name


                binding.progressBar.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
                binding.mainTemp.text = mainTempText
                binding.feelsLike.text = feelsLikeTempText
                binding.dayMaximumTemp.text = maximumTempText
                binding.dayMinimumTemp.text = minimumTempText
                binding.dateAndTime.text = dateText
                binding.weatherType.text = weatherTypeText
                binding.sunriseTimeText.text = sunriseText
                binding.sunsetTimeText.text = sunSetText
                binding.humidityText.text = humidityText
                binding.pressureText.text = pressureText
                binding.windSpeedText.text = windSpeedText
                binding.locationText.text = mLocationText

                binding.weatherIcon.load(weatherIcon)

                Log.d(TAG, "Weather Type icon link is: $weatherIcon")

            } catch (ex: Exception) {
                val feedbackText = "Failed to fetch the response, check internet connection"
                Log.e(TAG, feedbackText , ex)

                binding.progressBar.visibility = View.GONE
                binding.noInternetLayout.visibility = View.VISIBLE
                binding.textViewFeedback.text = feedbackText


            }
        }

    }




}
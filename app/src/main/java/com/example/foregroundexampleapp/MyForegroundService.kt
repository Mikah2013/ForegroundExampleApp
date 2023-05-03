package com.example.foregroundexampleapp

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.foregroundexampleapp.weathermodule.WeatherUtilities
import com.google.android.gms.location.*

class MyForegroundService : Service() {

    private val TAG = "MyService"
    private val binder = MyBinder()
    private lateinit var mainActivity: MainActivity
    private lateinit var weatherUtilities: WeatherUtilities
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mLocation: Location
    private lateinit var locationRequest: LocationRequest
    private var currentActivity: Activity? = null
    private lateinit var locationPreferences: SharedPreferences

    companion object {
        const val NOTIFICATION_ID = 1
        const val FINE_PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION
        const val COARSE_PERMISSION_STRING = Manifest.permission.ACCESS_COARSE_LOCATION
    }

    inner class MyBinder : Binder() {
        fun getService(): MyForegroundService = this@MyForegroundService
    }

    override fun onCreate() {
        super.onCreate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationPreferences = applicationContext.getSharedPreferences(R.string.my_location.toString(), Context.MODE_PRIVATE)
        mainActivity = MainActivity()
        weatherUtilities = WeatherUtilities()
        currentActivity = Activity()
        if (!mainActivity.isInFocus) {
            getLocationCallback()
            createLocationRequest()
            getLastLocation()
            createNotification( )
            startLocationUpdates()
        }
        Log.d(TAG, "Service onCreate")
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "Service bound")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (!mainActivity.isInFocus) {
            Log.d(TAG, "Service onUnbind method called")
            createNotification()
            startLocationUpdates()
        }
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        // Perform some background work here
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    private fun createLocationRequest() {
        Log.d(TAG, "Location Request Method called")
        locationRequest = LocationRequest()
        locationRequest.interval = 120000
        locationRequest.fastestInterval = 60000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun getLastLocation() {
        Log.d(TAG, "Get Last Location Method Called")
        try {
            mFusedLocationClient.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLocation = task.result
                        // I will be using the start the get locationCallback method to get location updates
                        val lon = mLocation.longitude
                        val lat = mLocation.latitude

                        Log.i("MyLocationService", "My Location is - $lat, $lon")
                        weatherUtilities.saveLocation(applicationContext, mLocation)

                    }
                    else {
                        Log.e("MyLocationService", "Failed to get location")
                    }
                }
        } catch (ex:SecurityException) {

            Log.e("MyLocationService", "" + ex.message)
        }
    }

    private fun getLocationCallback() {
        Log.d(TAG, "Get LocationCallback Method Called")
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0 ?: return
                for (location in p0.locations){
                    val lon = p0.lastLocation?.longitude
                    val lat = p0.lastLocation?.latitude
                    Log.i("MyLocationService", "My Location is is is - $lat, $lon")
                    weatherUtilities.saveLocation(applicationContext, p0.lastLocation)
                }
                //onNewLocation(p0!!.lastLocation)
            }
        }
    }

    fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, FINE_PERMISSION_STRING) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, COARSE_PERMISSION_STRING) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions(currentActivity!!)
            return
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel("my_channel", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        // Create the notification
        val notification = NotificationCompat.Builder(this, "my_channel")
            .setContentTitle("My Service")
            .setContentText("My Service is running in the background.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        // Start the service in the foreground with the notification
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun requestLocationPermissions(activity: Activity) {
        activity?.let {
            ActivityCompat.requestPermissions(
                it, arrayOf(FINE_PERMISSION_STRING,
                    COARSE_PERMISSION_STRING),
                mainActivity.PERMISSION_REQUEST_CODE)
        }
    }

}
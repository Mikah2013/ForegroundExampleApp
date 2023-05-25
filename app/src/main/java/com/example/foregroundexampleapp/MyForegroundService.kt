package com.example.foregroundexampleapp

import android.Manifest
import android.app.*
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
import android.widget.Toast
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
    private var isLocationTrackingEnabled = false

    companion object {
        const val NOTIFICATION_ID = 1
        const val FINE_PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION
        const val COARSE_PERMISSION_STRING = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val PACKAGE_NAME = "com.example.foregroundexampleapp"
        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"
        private const val EXTRA_START_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.START_LOCATION_TRACKING_FROM_NOTIFICATION"
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
            startLocationUpdates()
            isLocationTrackingEnabled = true
            createNotification()
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
            startLocationUpdates()
            createNotification()
        }
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        // Perform some background work here
        if (intent != null) {
            if (intent.getBooleanExtra(EXTRA_START_LOCATION_TRACKING_FROM_NOTIFICATION, false)) {
                startLocationTracking()
            } else if (intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)) {
                stopLocationTracking()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    private fun startLocationTracking() {
        if (!isLocationTrackingEnabled) {
            // Start location updates
            getLocationCallback()
            createLocationRequest()
            getLastLocation()
            startLocationUpdates()
            isLocationTrackingEnabled = true
            createNotification()
            Log.d(TAG, "Location Tracking Started")
        }
    }

    private fun stopLocationTracking() {
        if (isLocationTrackingEnabled) {
            // Stop location updates
            stopLocationUpdates()
            isLocationTrackingEnabled = false
            createNotification()
            Log.d(TAG, "Location Tracking Stopped")

        }
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

    private fun startLocationUpdates() {
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

        val startIntent = Intent(this, MainActivity::class.java)
        startIntent.putExtra(EXTRA_START_LOCATION_TRACKING_FROM_NOTIFICATION, true)
        val startPendingIntent = PendingIntent.getActivity(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val cancelIntent = Intent(this, MyForegroundService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)
        val cancelPendingIntent = PendingIntent.getService(this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val actionButtonIcon = if (isLocationTrackingEnabled)
            android.R.drawable.radiobutton_on_background
        else
            android.R.drawable.ic_menu_close_clear_cancel

        val actionButtonText = if (!isLocationTrackingEnabled)
            getString(R.string.start_location_updates_button_text)
        else
            getString(R.string.stop_location_updates_button_text)

        val pendingIntent = if (!isLocationTrackingEnabled)
            startPendingIntent
        else
            cancelPendingIntent


        // Create the notification
        val notification = NotificationCompat.Builder(this, "my_channel")
            .setContentTitle(getText(R.string.app_name))
            .setContentText("Location Tracking in the Background")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(
                actionButtonIcon,
                actionButtonText,
                pendingIntent
            )
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

    private fun stopLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, FINE_PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, COARSE_PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    /*private fun startLocationTracking() {
        if (!mainActivity.isInFocus && !isLocationTrackingEnabled) {
            // Start location updates
            getLocationCallback()
            createLocationRequest()
            getLastLocation()
            isLocationTrackingEnabled = true
            createNotification()
            startLocationUpdates()
            Log.d(TAG, "Location Tracking Started")
        }
    }*/

    /*private fun stopLocationTracking() {
        if (isLocationTrackingEnabled) {
            // Stop location updates
            stopLocationUpdates()
            isLocationTrackingEnabled = false
            createNotification()
            Log.d(TAG, "Location Tracking Stopped")

        }
    }*/


}
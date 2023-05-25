package com.example.foregroundexampleapp

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.foregroundexampleapp.databinding.ActivityMainBinding
import com.example.foregroundexampleapp.ui.*
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private var myService: MyForegroundService? = null
    private var isServiceBound = false
    var isInFocus = false
    var PERMISSION_REQUEST_CODE = 1234
    private val FINE_PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_PERMISSION_STRING = Manifest.permission.ACCESS_COARSE_LOCATION
    //private val BACKGROUND_PERMISSION_STRING = Manifest.permission.ACCESS_BACKGROUND_LOCATION

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyForegroundService.MyBinder
            myService = binder.getService()
            isServiceBound = true
            Log.d("MainActivity", "isServiceBound = true")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("MainActivity", "onCreate - isInFocus = false")
        checkInternetConnection(this)

        binding.apply {
            toggle = ActionBarDrawerToggle(
                this@MainActivity,
                drawerLayout,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.home_fragment -> {
                        replaceFragment(
                            HomeFragment(),
                            it.title.toString(),
                            R.id.home_fragment,
                            navView
                        )
                    }
                    R.id.compass_fragment -> {
                        replaceFragment(
                            CompassFragment(),
                            it.title.toString(),
                            R.id.compass_fragment,
                            navView
                        )
                    }
                    R.id.man_over_board_fragment -> {
                        replaceFragment(
                            CallForHelpFragment(),
                            it.title.toString(),
                            R.id.man_over_board_fragment,
                            navView
                        )
                    }
                    R.id.marine_tips_fragment -> {
                        replaceFragment(
                            MarineTipsFragment(),
                            it.title.toString(),
                            R.id.marine_tips_fragment,
                            navView
                        )
                    }
                    R.id.weather_fragment -> {
                        replaceFragment(
                            WeatherFragment(),
                            it.title.toString(),
                            R.id.weather_fragment,
                            navView
                        )
                    }
                }
                true
            }
        }


    }

    override fun onStart() {
        super.onStart()
        isInFocus = false
        if (checkLocationPermissions()) {
            if (isLocationEnabled()) {
                checkInternetConnection(this)
                Log.d("MainActivity", "Location Enabled")

            } else {
                Toast.makeText(applicationContext,"Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(intent)
            }
        } else {
            requestLocationPermissions()
        }
        Log.d("MainActivity", "onStart() - isInFocus = False")
    }

    override fun onResume() {
        super.onResume()
        isInFocus = true
        Log.d("MainActivity", "onResume() - isInFocus = true")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop() - no longer visible to the user.")
    }

    override fun onPause() {
        super.onPause()
        isInFocus = false
        Log.d("MainActivity", "onPause() - loses focus, but is still visible to the user")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            isInFocus = false
            unbindService(serviceConnection)
            isServiceBound = false
            Log.d("MainActivity", "onDestroy() - activity is destroyed and removed from memory")
        }
    }

    private fun bindService() {
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    bindService()
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    bindService()
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            } else {
                Log.i("Internet", "No Network Connection")
                bindService()
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkInternetConnection(this)
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkLocationPermissions() : Boolean {
        if (ContextCompat.checkSelfPermission(this, FINE_PERMISSION_STRING) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, COARSE_PERMISSION_STRING) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions()
        }
        return true
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(FINE_PERMISSION_STRING,
            COARSE_PERMISSION_STRING),
            PERMISSION_REQUEST_CODE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)
    }

    fun replaceFragment(fragment: Fragment,title: String,mMenuItemId: Int, navigationView: NavigationView ) {

        val fragmentM = supportFragmentManager
        val fragmentTransaction = fragmentM.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
        binding.drawerLayout.closeDrawers()
        setTitle(title)
        val menuItem = navigationView.menu.findItem(mMenuItemId)
        menuItem.isChecked = true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)

        } else {

            super.onBackPressed()
        }
    }

}
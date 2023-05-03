package com.example.foregroundexampleapp.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.example.foregroundexampleapp.databinding.FragmentCompassBinding

class CompassFragment : Fragment() {

    private lateinit var binding: FragmentCompassBinding
    private lateinit var mSensorManager: SensorManager
    private lateinit var mMagnetometer: Sensor
    private lateinit var mAccelerometer: Sensor
    private lateinit var mImageViewCompass: AppCompatImageView
    private var mGravityValues = floatArrayOf(0f, 0f, 0f)
    private var mAccelerationValues = floatArrayOf(0f, 0f, 0f)
    private var mRotationMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var mLastDirectionInDegrees = 0f

    private val mSensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            calculateCompassDirection(event)
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            //Nothing to do
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCompassBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        startSensor()
    }

    private fun startSensor() {
        mSensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    @Suppress("DEPRECATION")
    override fun onDestroyView() {
        mSensorManager.unregisterListener(mSensorListener)
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(mSensorListener)
    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(mSensorListener, mMagnetometer,
            SensorManager.SENSOR_DELAY_GAME)
        mSensorManager.registerListener(mSensorListener, mAccelerometer,
            SensorManager.SENSOR_DELAY_GAME)

    }

    private fun calculateCompassDirection(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> mAccelerationValues = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> mGravityValues = event.values.clone()
        }
        val success = SensorManager.getRotationMatrix(
            mRotationMatrix,
            null,
            mAccelerationValues,
            mGravityValues
        )
        if (success) {
            val orientationValues = FloatArray(3)
            SensorManager.getOrientation(mRotationMatrix, orientationValues)
            val azimuth = Math.toDegrees((-orientationValues[0]).toDouble()).toFloat()
            val rotateAnimation = RotateAnimation(
                mLastDirectionInDegrees,
                azimuth,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotateAnimation.duration = 50
            rotateAnimation.fillAfter = true
            mImageViewCompass.startAnimation(rotateAnimation)
            mLastDirectionInDegrees = azimuth
        }
    }
}



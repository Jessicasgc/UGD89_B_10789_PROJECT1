package com.example.ugd89_b_10789_project1;

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.hardware.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ugd89_b_10789_project1.databinding.ActivityMainBinding
import java.lang.Math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener{
    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null
    private var currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var binding: ActivityMainBinding? = null
    private val CHANNEL_ID_1 = "channel_notification_01"
    private val notificationId1 = 101

    private lateinit var square: TextView
    // on below line we are creating
    // a variable for our text view.
    lateinit var sensorStatusTV: TextView

    // on below line we are creating
    // a variable for our proximity sensor
    lateinit var proximitySensor: Sensor

    // on below line we are creating
    // a variable for sensor manager
    lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        createNotificationChannel()

        // on below line we are initializing our all variables.
        sensorStatusTV = findViewById(R.id.idTVSensorStatus)

        // on below line we are initializing our sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // on below line we are initializing our proximity sensor variable
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)


        // on below line we are checking if the proximity sensor is null
        if (proximitySensor == null) {
            // on below line we are displaying a toast if no sensor is available
            Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // on below line we are registering
            // our sensor with sensor manager
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
            try {
                mCamera = Camera.open()

            } catch (e: Exception) {
                Log.d("Error", "Failed to get Camera" + e.message)
            }
            if (mCamera != null) {
                mCameraView = CameraView(this, mCamera!!)
                val camera_view = findViewById<View>(R.id.FlCamera) as FrameLayout
                camera_view.addView(mCameraView)
            }

            @SuppressLint("MissingInflateId", "LocalSurppress") val imageClose =
                findViewById<View>(R.id.imgClose) as ImageButton
            imageClose.setOnClickListener { view: View? -> System.exit(0) }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setUpSensorStuff()

    }
    private fun setUpSensorStuff() {
        // Create the sensor manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Specify the sensor you want to listen to
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }
    // calling the sensor event class to detect
    // the change in data when sensor starts working.
    fun changeCamera() {
        try {
            mCamera?.stopPreview()
        } catch (e: Exception) {
            e.printStackTrace();
        }
        mCamera?.release()

        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK
        }

        try {
            mCamera = Camera.open(currentCameraId)
        } catch (e: Exception) {
            Log.d("Error", "Failed to get Camera" + e.message)
        }
        if (mCamera != null) {
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FlCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }
    }
    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // method to check accuracy changed in sensor.
        }

        override fun onSensorChanged(event: SensorEvent) {
            // check if the sensor type is proximity sensor.
            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0f) {
                    // here we are setting our status to our textview..
                    // if sensor event return 0 then object is closed
                    // to sensor else object is away from sensor.

                    changeCamera()


                }




                    // on below line we are setting text for text view
                    // as object is away from sensor.


                }
            }
        }
    override fun onSensorChanged(event: SensorEvent?) {
        // Checks for the sensor we have registered
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            //Log.d("Main", "onSensorChanged: sides ${event.values[0]} front/back ${event.values[1]} ")
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            // with the help of fetched x,y,z values
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Display a Toast message if
            // acceleration value is over 12
            if (acceleration > 12) {
                sendNotification2()
            }
            // Sides = Tilting phone left(10) and right(-10)

        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"

            val channel1 = NotificationChannel(CHANNEL_ID_1, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
            }
    }
    private fun sendNotification2() {

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_baseline_looks_two_24)
            .setContentTitle("Modul89_B_10789_PROJECT2")
            .setContentText("Selamat anda sudah berhasil Modul 8 dan 9")
            .setPriority(NotificationCompat.PRIORITY_LOW)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId1, builder.build())
        }
    }
}




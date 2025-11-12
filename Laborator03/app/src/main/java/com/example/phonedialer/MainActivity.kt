package com.example.phonedialer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity(), SensorEventListener {
    private var PhoneEditText : EditText? = null
    private var callImageButton: ImageButton? = null
    private var hangupImageButton: ImageButton? = null
    private var backspaceImageButton: ImageButton? = null
    private var genericButton: Button? = null

    private var accelerationTextView: TextView? = null

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null


    private val callButtonListener = callButtonListener()

    companion object {
        private const val PERMISSION_REQUEST_CALL_PHONE = 1
        const val CONTACTS_MANAGER_REQUEST_CODE = 2017
    }

    private val buttonIds = intArrayOf(
        R.id.number_0_button,
        R.id.number_1_button,
        R.id.number_2_button,
        R.id.number_3_button,
        R.id.number_4_button,
        R.id.number_5_button,
        R.id.number_6_button,
        R.id.number_7_button,
        R.id.number_8_button,
        R.id.number_9_button,
        R.id.star_button,
        R.id.diez_button
    )

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // --- implementare SensorEventListener ---
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val xAccel = it.values[0]
            accelerationTextView?.text = "Accel X: $xAccel"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // nu facem nimic aici
    }

    private inner class callButtonListener: View.OnClickListener {
        override fun onClick(view: View) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    PERMISSION_REQUEST_CALL_PHONE
                )
            } else {
                val intent = Intent(Intent.ACTION_CALL)
                intent.setData(Uri.parse("tel:" + PhoneEditText!!.text.toString()))
                startActivity(intent)
            }
        }


    }

    private val hangupImageButtonClickListener = HangupImageButtonClickListener()

    private inner class HangupImageButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            finish()
        }
    }

    private val backspaceButtonClickListener = BackspaceButtonClickListener()

    private inner class BackspaceButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            var phoneNumber = PhoneEditText!!.text.toString()
            if (phoneNumber.isNotEmpty()) {
                phoneNumber = phoneNumber.substring(0, phoneNumber.length - 1)
                PhoneEditText!!.setText(phoneNumber)
            }
        }
    }

    private val contactsManagerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            // aici poți prelucra datele returnate, dacă e cazul
            Toast.makeText(this, "Contact salvat cu succes", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Operațiune anulată", Toast.LENGTH_SHORT).show()
        }
    }
//    private val contactsImageButtonClickListener = View.OnClickListener {
//        val phoneNumber = PhoneEditText?.text.toString()
//        if (phoneNumber.isNotEmpty()) {
//            val intent = Intent("ro.pub.cs.systems.eim.lab04.contactsmanager.intent.action.ContactsManagerActivity").apply {
//                putExtra("ro.pub.cs.systems.eim.lab04.contactsmanager.PHONE_NUMBER_KEY", phoneNumber)
//            }
//            startActivityForResult(intent,CONTACTS_MANAGER_REQUEST_CODE)
//        } else {
//            Toast.makeText(applicationContext, getString(R.string.phone_error), Toast.LENGTH_LONG).show()
//        }
//    }

    private val contactsImageButtonClickListener = View.OnClickListener {
        val phoneNumber = PhoneEditText?.text.toString()
        if (phoneNumber.isNotEmpty()) {
            val intent = Intent("ro.pub.cs.systems.eim.lab04.contactsmanager.intent.action.ContactsManagerActivity").apply {
                putExtra("ro.pub.cs.systems.eim.lab04.contactsmanager.PHONE_NUMBER_KEY", phoneNumber)
            }
            // 2. Folosește launcher-ul în loc de startActivityForResult
            contactsManagerLauncher.launch(intent)
        } else {
            Toast.makeText(applicationContext, getString(R.string.phone_error), Toast.LENGTH_LONG).show()
        }
    }

    private val genericButtonClickListener = GenericButtonClickListener()

    private inner class GenericButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            PhoneEditText!!.setText(PhoneEditText!!.text.toString() + (view as Button).text.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        accelerationTextView = findViewById(R.id.acceleration_text_view)

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        PhoneEditText = findViewById<View>(R.id.phoneNumber) as EditText
        callImageButton = findViewById<View>(R.id.call_image_button) as ImageButton
        callImageButton!!.setOnClickListener(callButtonListener)
        hangupImageButton = findViewById<View>(R.id.decline_image_button) as ImageButton
        hangupImageButton!!.setOnClickListener(hangupImageButtonClickListener)
        backspaceImageButton = findViewById<View>(R.id.delete_image_button) as ImageButton
        backspaceImageButton!!.setOnClickListener(backspaceButtonClickListener)
        for (index in buttonIds.indices) {
            genericButton = findViewById(buttonIds[index])
            genericButton?.setOnClickListener(genericButtonClickListener)
        }

        findViewById<ImageButton>(R.id.contacts_image_button).setOnClickListener(contactsImageButtonClickListener)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

}
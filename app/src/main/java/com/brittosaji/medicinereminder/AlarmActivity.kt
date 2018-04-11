package com.brittosaji.medicinereminder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_alarm.*

class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        val receivedIntent=intent
        Log.i("Intent Test","${receivedIntent?.extras?.getString("medName")}")
        medicineNameAlarm.text=receivedIntent?.extras?.getString("medName")
    }
}

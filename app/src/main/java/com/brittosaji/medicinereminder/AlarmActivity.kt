package com.brittosaji.medicinereminder

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_alarm.*
import java.util.*

class AlarmActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    lateinit var currentUserRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm)

        mAuth= FirebaseAuth.getInstance()

        //Initialize Firebase Database Instance
        db= FirebaseDatabase.getInstance()
        userRef=db.getReference()
        currentUserRef=userRef.child("users").child(mAuth.currentUser?.uid)

        val receivedIntent=intent
        Log.i("Intent Test","${receivedIntent?.extras?.getString("medName")}")
        medicineNameAlarm.text=receivedIntent?.extras?.getString("medName")
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    Toast.makeText(this@AlarmActivity, "Alarm Missed", Toast.LENGTH_LONG).show()
                    val missedRef=currentUserRef.child("missed_alarms").push()
                    missedRef.child("name").setValue(medicineNameAlarm.text)
                    val cal=Calendar.getInstance()
                    missedRef.child("time").setValue(cal.time.hours.toString()+":"+cal.time.minutes.toString())

                }
            }
        },15000)
        dismissButton.setOnClickListener {
            timer.cancel()
            finish()
        }
    }
}

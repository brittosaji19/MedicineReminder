package com.brittosaji.medicinereminder

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MissedService : Service() {
    private lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    lateinit var currentUserRef: DatabaseReference
    lateinit var patientsRef:DatabaseReference
    lateinit var users: DataSnapshot
    lateinit var patients:DataSnapshot
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mAuth= FirebaseAuth.getInstance()

        db= FirebaseDatabase.getInstance()
        userRef=db.getReference()
        currentUserRef=userRef.child("users").child(mAuth.currentUser?.uid)
        patientsRef=currentUserRef.child("patients")

        patientsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //var value:User? = dataSnapshot.getValue(User::class.java)
                patients = dataSnapshot
                //updateUI()
                for (keys in patients.children) {
                    //Log.d("Output",data.value.toString())
                    val currentPatientID = keys.child("uid").value.toString()
                    userRef.child("users").child(currentPatientID).child("missed_alarms").addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot?) {
                            val notificationBuilder= NotificationCompat.Builder(applicationContext)
                            val missedServiceIntent=Intent(this@MissedService,MissedAlarms::class.java)
                            missedServiceIntent.putExtra("user",keys.child("uid").value.toString())
                            val pendingIntent= PendingIntent.getActivity(this@MissedService,1,missedServiceIntent, PendingIntent.FLAG_ONE_SHOT)
                            notificationBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Medicine Reminder").setContentText("Your Patient Missed An Alarm").setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS).setContentIntent(pendingIntent)
                            val notificationManager=applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            if (p0?.childrenCount!! >0) {
                                notificationManager.notify(1, notificationBuilder.build())
                            }
                        }

                        override fun onCancelled(error: DatabaseError?) {
                            Log.w("Error", "Failed to read value.", error?.toException())                                }
                    })

                }



            }
            override fun onCancelled(error: DatabaseError?) {
                Log.w("Error", "Failed to read value.", error?.toException())
            }

        })

            return START_STICKY
    }

}

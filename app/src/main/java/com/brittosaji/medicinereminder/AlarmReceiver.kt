package com.brittosaji.medicinereminder

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast

/**
 * Created by britto on 11/4/18.
 */
class AlarmReceiver:BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context,"Alarm Alarm",Toast.LENGTH_LONG).show()
        val receivedintent=intent
        val medName=intent?.extras?.getString("name")
        val alarmActivityIntent=Intent(context,AlarmActivity::class.java)
        Log.i("Receive Test","Here: $medName")
        alarmActivityIntent.putExtra("medName","$medName")
        //context?.startActivity(alarmActivityIntent)
        val pendingIntent=PendingIntent.getActivity(context,0,alarmActivityIntent,PendingIntent.FLAG_ONE_SHOT)
        val notificationBuilder=NotificationCompat.Builder(context)
        notificationBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Medicine Reminder").setContentText("Time To Take Your Meds").setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS).setContentIntent(pendingIntent)
        val notificationManager=context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1,notificationBuilder.build())
    }
}
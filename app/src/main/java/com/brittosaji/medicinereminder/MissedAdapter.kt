package com.brittosaji.medicinereminder

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.missed_alarm.view.*
import kotlinx.android.synthetic.main.patient_info.view.*
import java.util.ArrayList

/**
 * Created by britto on 12/4/18.
 */
class MissedAdapter(val activity: Activity, val missedData: ArrayList<MissedAdapter.MissedData>):BaseAdapter(){
    private lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    lateinit var currentUserRef: DatabaseReference
    lateinit var patientsRef: DatabaseReference
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        mAuth= FirebaseAuth.getInstance()

        //Initialize Firebase Database Instance
        db= FirebaseDatabase.getInstance()
        userRef=db.getReference()
        currentUserRef=userRef.child("users").child(mAuth.currentUser?.uid)
        patientsRef=currentUserRef.child("patients")
        val rootView: View =activity.layoutInflater.inflate(R.layout.missed_alarm,p2,false)
        rootView.missedNameTextView.text=missedData[p0].name
        rootView.missedTime.text=missedData[p0].time

        rootView.dismissMissed.setOnClickListener {
            userRef.child("users").child(missedData[p0].uid).child("missed_alarms").child(missedData[p0].missedID).removeValue()
            Log.i("MissedAdapter",missedData[p0].missedID)
            activity.recreate()
        }


        return rootView
    }

    override fun getItem(p0: Int): Any {
        return missedData[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return missedData.size
    }
    data class MissedData(val name:String,val time:String,val uid:String,val missedID:String)
}
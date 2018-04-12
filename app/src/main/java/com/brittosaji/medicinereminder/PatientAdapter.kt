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
import kotlinx.android.synthetic.main.patient_info.view.*
import java.util.ArrayList

/**
 * Created by britto on 12/4/18.
 */
class PatientAdapter(val activity: Activity, val patientData: ArrayList<PatientAdapter.PatientData>):BaseAdapter(){
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
        val rootView:View=activity.layoutInflater.inflate(R.layout.patient_info,p2,false)
        rootView.patientName.text=patientData[p0].patientName
        rootView.patientEmail.text=patientData[p0].patientEmail

        rootView.removePatientButton.setOnClickListener {
            Toast.makeText(activity,"Remove Button Clicked", Toast.LENGTH_LONG).show()
            patientsRef.child(patientData[p0].uid).removeValue().addOnSuccessListener {
                Toast.makeText(activity,"Removed Successfully", Toast.LENGTH_SHORT).show()
            }
            activity.recreate()
        }


        return rootView
    }

    override fun getItem(p0: Int): Any {
        return patientData[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return patientData.size
    }
    data class PatientData(val patientName:String,val patientEmail:String,val patientID:String,val uid:String)
}
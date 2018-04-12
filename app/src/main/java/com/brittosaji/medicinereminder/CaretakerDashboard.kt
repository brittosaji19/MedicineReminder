package com.brittosaji.medicinereminder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_caretaker_dashboard.*
import kotlinx.android.synthetic.main.activity_manage_reminder.*

class CaretakerDashboard : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    lateinit var currentUserRef: DatabaseReference
    lateinit var patientsRef: DatabaseReference
    lateinit var patients:DataSnapshot
    lateinit var users:DataSnapshot
    lateinit var list:ArrayList<PatientAdapter.PatientData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caretaker_dashboard)
        list= ArrayList()

        mAuth= FirebaseAuth.getInstance()

        //Initialize Firebase Database Instance
        db= FirebaseDatabase.getInstance()
        userRef=db.getReference()
        currentUserRef=userRef.child("users").child(mAuth.currentUser?.uid)
        patientsRef=currentUserRef.child("patients")

        patientsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //var value:User? = dataSnapshot.getValue(User::class.java)
                patients=dataSnapshot
                //updateUI()
                userRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        //var value:User? = dataSnapshot.getValue(User::class.java)
                        users=dataSnapshot
                        updateUI()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w("Error", "Failed to read value.", error.toException())
                    }
                })



            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException())
            }
        })



        addPatientButton.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1);
            else
                startActivity(Intent(this, ScannerActivity::class.java))

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startActivity(Intent(this, ScannerActivity::class.java))
        }else
            Toast.makeText(this, "Scanner won't work without permission!!!!!", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(){
        for (keys in patients.children) {
            //Log.d("Output",data.value.toString())
            val currentPatientID=keys.child("uid").value.toString()
            val data = Patient(users.child(currentPatientID).child("name").value.toString(),users.child(currentPatientID).child("email").value.toString(), users.child(currentPatientID).child("id").value.toString())

            Toast.makeText(this,"UpdateUITest",Toast.LENGTH_LONG).show()
            list.add(PatientAdapter.PatientData(data.name,data.email,data.ID,keys.key))
            Log.i("PatientData",data.name+" : "+data.email+"")


        }
        patientsListView.adapter=PatientAdapter(this@CaretakerDashboard,list)
    }

}

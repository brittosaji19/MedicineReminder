package com.brittosaji.medicinereminder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_manage_reminder.*
import java.util.ArrayList

class ManageReminder : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    lateinit var currentUserRef: DatabaseReference
    lateinit var medicineRef: DatabaseReference
    private lateinit var reminders: DataSnapshot
    val remindersList= ArrayList<Reminder>()
    val list = ArrayList<ManageAdapter.ManageData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_reminder)
        mAuth= FirebaseAuth.getInstance()

        //Initialize Firebase Database Instance
        db= FirebaseDatabase.getInstance()
        userRef=db.getReference()
        currentUserRef=userRef.child("users").child(mAuth.currentUser?.uid)
        medicineRef=currentUserRef.child("reminders")

        medicineRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //var value:User? = dataSnapshot.getValue(User::class.java)
                reminders=dataSnapshot
                updateUI()

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException())
            }
        })



    }
    private fun updateUI() {
        for (keys in reminders.children) {
            //Log.d("Output",data.value.toString())
            val data = Reminder(keys.child("name").value.toString(), keys.child("time").value.toString(), keys.child("unit").value.toString())
            remindersList.add(data)
            Toast.makeText(this,"UpdateUITest",Toast.LENGTH_LONG).show()
            list.add(ManageAdapter.ManageData(data.name,data.time,data.unit,keys.key))
            Log.d("Output",keys.key)

        }
        manageList.adapter=ManageAdapter(this@ManageReminder,list)
    }
}

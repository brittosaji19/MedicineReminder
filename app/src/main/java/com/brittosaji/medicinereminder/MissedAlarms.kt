package com.brittosaji.medicinereminder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_missed_alarms.*

class MissedAlarms : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    lateinit var currentUserRef: DatabaseReference
    lateinit var users: DataSnapshot
    lateinit var list:ArrayList<MissedAdapter.MissedData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_missed_alarms)
        list= ArrayList()
        mAuth= FirebaseAuth.getInstance()

        db= FirebaseDatabase.getInstance()
        userRef=db.getReference()
        currentUserRef=userRef.child("users").child(mAuth.currentUser?.uid)

        userRef.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //var value:User? = dataSnapshot.getValue(User::class.java)
                users=dataSnapshot
                val user=intent.extras.getString("user")
                updateUI(user)

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException())
            }
        })



    }

    private fun updateUI(user:String){
        val missed_alarms=users.child(user).child("missed_alarms")
        for (keys in missed_alarms.children) {
            //Log.d("Output",data.value.toString())


            list.add(MissedAdapter.MissedData(keys.child("name").value.toString(),keys.child("time").value.toString(),user,keys.key))


        }
        missedListView.adapter=MissedAdapter(this,list)
    }
}

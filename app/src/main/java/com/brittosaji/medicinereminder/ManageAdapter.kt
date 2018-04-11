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
import kotlinx.android.synthetic.main.manage_item.view.*
import java.util.ArrayList

/**
 * Created by britto on 11/4/18.
 */
class ManageAdapter(val activity:Activity,val manageData: ArrayList<ManageAdapter.ManageData>):BaseAdapter(){
    private lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    lateinit var currentUserRef: DatabaseReference
    lateinit var medicineRef: DatabaseReference
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        mAuth= FirebaseAuth.getInstance()

        //Initialize Firebase Database Instance
        db= FirebaseDatabase.getInstance()
        userRef=db.getReference()
        currentUserRef=userRef.child("users").child(mAuth.currentUser?.uid)
        medicineRef=currentUserRef.child("reminders")
        val rootView:View=activity.layoutInflater.inflate(R.layout.manage_item,p2,false)
        rootView.medName.text=manageData[p0].medName
        rootView.repNo.text=manageData[p0].repNo
        rootView.repUnit.text=manageData[p0].repUnit
        rootView.removeReminderButton.setOnClickListener {
            Toast.makeText(activity,"Remove Button Clicked",Toast.LENGTH_LONG).show()
            Log.i("ID Test",manageData[p0].uid)
            medicineRef.child(manageData[p0].uid).removeValue().addOnSuccessListener {
                Toast.makeText(activity,"Removed Successfully",Toast.LENGTH_SHORT).show()
            }
            activity.recreate()
        }
        return rootView

    }

    override fun getItem(p0: Int): Any {
        return manageData[p0]
    }

    override fun getItemId(p0: Int): Long {

        return p0.toLong()
    }

    override fun getCount(): Int {

        return manageData.size
    }
    data class ManageData(val medName:String,val repNo:String,val repUnit:String,val uid:String)
}
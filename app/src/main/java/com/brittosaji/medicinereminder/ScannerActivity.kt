package com.brittosaji.medicinereminder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import kotlinx.android.synthetic.main.activity_scanner.*

class ScannerActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    lateinit var currentUserRef: DatabaseReference
    lateinit var patientsRef:DatabaseReference

    var barcodeview: CompoundBarcodeView? = null

    var callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            Log.d("Scanned Item", result?.result.toString())

            mAuth= FirebaseAuth.getInstance()

            //Initialize Firebase Database Instance
            db= FirebaseDatabase.getInstance()
            userRef=db.getReference()
            currentUserRef=userRef.child("users").child(mAuth.currentUser?.uid)
            patientsRef=currentUserRef.child("patients")
            val currentPatient=patientsRef.push().child("uid").setValue(result?.result.toString())
            finish()

        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        barcodeview = barcode_scanner

        barcodeview?.decodeSingle(callback)

    }

    override fun onResume() {
        super.onResume()
        barcodeview?.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeview?.pause()
    }
}

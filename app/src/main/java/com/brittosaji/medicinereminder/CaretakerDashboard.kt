package com.brittosaji.medicinereminder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_caretaker_dashboard.*

class CaretakerDashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caretaker_dashboard)


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

}

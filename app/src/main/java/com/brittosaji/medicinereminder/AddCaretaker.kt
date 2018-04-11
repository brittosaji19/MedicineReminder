package com.brittosaji.medicinereminder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_add_caretaker.*


class AddCaretaker : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_caretaker)
        mAuth= FirebaseAuth.getInstance()

        qrImageView.setImageBitmap(QRCodeHandler.generateQRCode(mAuth.currentUser?.uid,getScreenRes()))
    }

    fun getScreenRes(): Int {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.densityDpi
    }
}

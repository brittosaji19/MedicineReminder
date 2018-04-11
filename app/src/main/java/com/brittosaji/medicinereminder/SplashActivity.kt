package com.brittosaji.medicinereminder

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.ActionBar
import android.view.Window
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    val mAuth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        var actionbar:ActionBar?=supportActionBar
        supportActionBar?.hide()
        var handler : Handler= Handler()
        handler.postDelayed(Runnable {
//            var toast=Toast.makeText(this,"Test Toast",Toast.LENGTH_LONG)
//            toast.show()
            if (mAuth.currentUser==null){
                var loginIntent=Intent(this,LoginActivity::class.java)
                startActivity(loginIntent)
            }
            else {
                var intentForMainActivity = Intent(this, MainActivity::class.java)
                startActivity(intentForMainActivity)
            }

        },1000)

    }
}

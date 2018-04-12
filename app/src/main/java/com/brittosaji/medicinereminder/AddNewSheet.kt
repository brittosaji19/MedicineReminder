package com.brittosaji.medicinereminder

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.brittosaji.medicinereminder.ocr.OcrCaptureActivity
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.add_new.*

/**
 * Created by britto on 19/1/18.
 */
class AddNewSheet:BottomSheetDialogFragment(){
    lateinit var timeEditText:EditText
    lateinit var timeUnitButton:Button
    lateinit var medicineNameEditText:EditText
    lateinit var db:FirebaseDatabase
    lateinit var userRef:DatabaseReference
    lateinit var mAuth:FirebaseAuth
    lateinit var currentUserRef:DatabaseReference
    lateinit var medicineRef:DatabaseReference
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)
        val rootView = View.inflate(context, R.layout.add_new, null)

        dialog!!.setContentView(
                rootView
        )

        //Auth
        mAuth= FirebaseAuth.getInstance()

        //Init Database ..Firebase..
        db= FirebaseDatabase.getInstance()
        userRef=db.reference
        currentUserRef=userRef.child("users").child(mAuth.currentUser?.uid)
        medicineRef=currentUserRef.child("reminders")

        timeEditText=rootView.findViewById(R.id.timeEditTextt)
        timeUnitButton=rootView.findViewById(R.id.timeUnitButton)
        medicineNameEditText=rootView.findViewById(R.id.medicineNameEditText)
        timeUnitButton.setOnClickListener({
            cycleUnits()
        })
        val addButton: Button =rootView.findViewById(R.id.addMedicineButton)
        addButton.setOnClickListener({
            addData()
        })

        (rootView.findViewById<Button>(R.id.scanNameButton)).setOnClickListener {
            startScanner()
        }



    }

    private val RC_OCR_CAPTURE: Int = 5

    private fun startScanner() {

        // launch Ocr capture activity.
        val intent = Intent(activity, OcrCaptureActivity::class.java)
        intent.putExtra(OcrCaptureActivity.AutoFocus, true)
        intent.putExtra(OcrCaptureActivity.UseFlash, false)

        startActivityForResult(intent, RC_OCR_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val name = data.getStringExtra(OcrCaptureActivity.TextBlockObject)

                    medicineNameEditText.setText(name)


                } else {
                    Toast.makeText(activity, R.string.ocr_failure, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(activity, R.string.ocr_error, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun addData():Unit{
        try {

            var timeData:Int=timeEditText.text.toString().toInt()
            var timeUnit:String=timeUnitButton.text.toString()
            var medicineName:String=medicineNameEditText.text.toString()
            val reminder=medicineRef.push()
            reminder.child("name").setValue(medicineName)
            reminder.child("time").setValue(timeData)
            reminder.child("unit").setValue(timeUnit)
            startActivity(Intent(context,MainActivity::class.java))
            dismiss()
            val toast: Toast = Toast.makeText(context,"Every ${timeData} ${timeUnit}", Toast.LENGTH_SHORT)
            toast.show()
        }
        catch (e:Exception){
            val toast: Toast = Toast.makeText(context,"Time Cannot Be Empty", Toast.LENGTH_SHORT)
            toast.show()
        }

    }

    fun cycleUnits():Unit{
        val units= arrayOf("Minutes","Hours","Days")
        val unit:String=timeUnitButton.text.toString()
        var index=units.indexOf(unit)
        index=(index+1)%3
        timeUnitButton.text=units[index]
    }
}
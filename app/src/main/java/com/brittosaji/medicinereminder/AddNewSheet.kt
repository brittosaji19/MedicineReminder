package com.brittosaji.medicinereminder

import android.annotation.SuppressLint
import android.app.Dialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.add_new.*

/**
 * Created by britto on 19/1/18.
 */
class AddNewSheet:BottomSheetDialogFragment(){
    @SuppressLint("RestrictedApi")
    lateinit var timeEditText:EditText
    lateinit var timeUnitButton:Button
    lateinit var medicineNameEditText:EditText
    lateinit var db:FirebaseDatabase
    lateinit var userRef:DatabaseReference
    lateinit var mAuth:FirebaseAuth
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)
        val rootView = View.inflate(context, R.layout.add_new, null)

        dialog!!.setContentView(
                rootView
        )

        //Auth
        mAuth= FirebaseAuth.getInstance()

        //Init Database ..Firebase..


        timeEditText=rootView.findViewById(R.id.timeEditTextt)
        timeUnitButton=rootView.findViewById(R.id.timeUnitButton)
        medicineNameEditText=rootView.findViewById(R.id.medicineNameEditText)
        timeUnitButton.setOnClickListener(View.OnClickListener {
            cycleUnits()
        })
        val addButton: Button =rootView.findViewById(R.id.addMedicineButton)
        addButton.setOnClickListener(View.OnClickListener {
            addData()
        })



    }
    fun addData():Unit{
        try {
            var timeData:Int=timeEditText.text.toString().toInt()
            var timeUnit:String=timeUnitButton.text.toString()
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
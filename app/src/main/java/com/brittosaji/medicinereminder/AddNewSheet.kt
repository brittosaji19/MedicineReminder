package com.brittosaji.medicinereminder

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.TabLayout
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import com.brittosaji.medicinereminder.ocr.OcrCaptureActivity
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_new.*
import kotlinx.android.synthetic.main.add_new.view.*
import org.jsoup.Jsoup
import java.util.*

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
    var alarmCount:Int?=0
    var manual:Int=0



    class RetreiveInfo(val context:Context?):AsyncTask<String,Void,String>(){
        lateinit var data:String
        override fun doInBackground(vararg params: String?): String {
            data=""
            try {
                val url="https://druginfo.nlm.nih.gov/drugportal/name/"+params[0]
                val document=Jsoup.connect(url).get()
                data=document.select("table.search-results").select("tr")[2].select("td")[1].text()
                Log.d("DataDoc",data)
            }
            catch (e:Exception){
                Log.i("InfoErr",e.message)
            }

            //Log.i("Doc:",document.toString())
            return "Done"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val infoIntent=Intent(context,InfoActivity::class.java)
            if (data==""){
                infoIntent.putExtra("desc","No Info Found")
            }
            else{
                infoIntent.putExtra("desc",data)
            }
            context?.startActivity(infoIntent)

        }
    }



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

        rootView.getInfoButton.setOnClickListener {
            displayInfo(rootView)
        }

        val timePickerButton=rootView.timePickerButton
        timePickerButton.setOnClickListener {
            showTimePickerDialog(rootView)
        }


    }

    private fun displayInfo(rootView: View?) {
        val searchKey=rootView?.medicineNameEditText?.text
        if (searchKey!=null){
            RetreiveInfo(context).execute(searchKey.toString())
        }
        else{
            Toast.makeText(context,"Empty Name",Toast.LENGTH_SHORT).show()
        }


    }
    var hr:Int=0
    var min:Int=0
    private fun showTimePickerDialog(rootView: View) {
        //Init SharedPreferences
        val sharedPref=context?.getSharedPreferences("ALARM_PREF",0)
        alarmCount=sharedPref?.getInt("count",0)
        val cal=Calendar.getInstance()
        val timePickerDialog=TimePickerDialog(context,TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            hr=hourOfDay
            min=minute
            var alarmString=""
            if (hourOfDay.toString().length==1){
                alarmString+="0"+hourOfDay.toString()
            }
            else{
                alarmString+=hourOfDay.toString()
            }
            alarmString+=":"
            if (minute.toString().length==1){
                alarmString+="0"+minute.toString()
            }
            else{
                alarmString+=minute.toString()
            }
            manual=1


            rootView.addMedicineButton.setOnClickListener {
                addManual(alarmString,if (hourOfDay>12) "PM" else "AM",rootView)
            }
            rootView.manualSetterClock.text=alarmString
            rootView.intervalSetter.visibility=View.INVISIBLE
            rootView.manualSetterClock.visibility=View.VISIBLE


        },cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true)
        timePickerDialog.setTitle("Choose Time")
        timePickerDialog.show()
    }

    fun addManual(alarmString: String, ampm: String, rootView: View) {
        val nameString=rootView.medicineNameEditText.text?:"No Name Entered"
        val sharedPref=context?.getSharedPreferences("ALARM_PREF",0)?.edit()
        if (sharedPref!=null){
            val count:Int=alarmCount?:0
            sharedPref.putString("alarm_time_"+alarmCount,alarmString)
            sharedPref.putString("alarm_name_"+alarmCount,nameString.toString())
            sharedPref.putString("alarm_ampm_"+alarmCount,ampm)
            sharedPref.putInt("count",count+1)
            Toast.makeText(context,alarmString,Toast.LENGTH_SHORT).show()
            Log.i("Count:",count.toString())
        }
        sharedPref?.apply()
        setAlarm(nameString.toString())
        startActivity(Intent(context,MainActivity::class.java))
        dismiss()
    }

    fun setAlarm(name:String){
        val alarmManager=context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar=Calendar.getInstance()
        //calendar.timeZone= TimeZone.getTimeZone("IST")
        val cal2=Calendar.getInstance()
        calendar.set(cal2.get(Calendar.YEAR),cal2.get(Calendar.MONTH),cal2.get(Calendar.DATE),hr,min,0)
        val alarmIntent=Intent(context,AlarmReceiver::class.java)
        Log.i("Extra Name",name)
        alarmIntent.putExtra("name",name)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context,0,alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pendingIntent)
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
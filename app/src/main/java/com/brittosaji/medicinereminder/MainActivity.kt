package com.brittosaji.medicinereminder
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.CompactDecimalFormat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.TimeUtils
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_new.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.navigation_main.*
import org.intellij.lang.annotations.JdkConstants
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*

class MainActivity : AppCompatActivity() {



    private lateinit var mAuth:FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    lateinit var currentUserRef:DatabaseReference
    lateinit var medicineRef:DatabaseReference
    private lateinit var reminders: DataSnapshot
    val remindersList= ArrayList<Reminder>()
    val alarmsList=ArrayList<Alarm>()
    val list = ArrayList<AlarmAdapter.AlarmsData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val appBar:Toolbar=appbar
        //setSupportActionBar(appBar)
        initializeDrawer()

        //Getting Firebase Instance
        mAuth= FirebaseAuth.getInstance()

        //Initialize Firebase Database Instance
        db= FirebaseDatabase.getInstance()
        userRef=db.getReference()
        currentUserRef=userRef.child("users").child(mAuth.currentUser?.uid)
        medicineRef=currentUserRef.child("reminders")
        addCaregiver.setOnClickListener {
            startActivity(Intent(this,AddCaretaker::class.java))
        }
        manageButton.setOnClickListener {
            startActivity(Intent(this,ManageReminder::class.java))
        }

        CGDashboardButton.setOnClickListener {
            startActivity(Intent(this,CaretakerDashboard::class.java))
        }

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

        val logOutButton=logOutButton
        logOutButton.setOnClickListener(View.OnClickListener {
            signOut();
        })



//        list.add(AlarmAdapter.AlarmsData("10:15","PM","Adol"))
//        list.add(AlarmAdapter.AlarmsData("11:15","PM","Adol"))
//        list.add(AlarmAdapter.AlarmsData("10:10","AM","Adol"))
//        list.add(AlarmAdapter.AlarmsData("9:15","PM","Adol"))
//        list.add(AlarmAdapter.AlarmsData("10:15","PM","Adol"))
//        list.add(AlarmAdapter.AlarmsData("11:15","PM","Adol"))
//        list.add(AlarmAdapter.AlarmsData("10:10","AM","Adol"))
//        list.add(AlarmAdapter.AlarmsData("9:15","PM","Adol"))




    }

    private lateinit var mDrawerLayout: DrawerLayout

    private lateinit var mDrawerToggle: ActionBarDrawerToggle

    private fun initializeDrawer() {
        mDrawerLayout = findViewById(R.id.parent_view)
        /*mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START)
        mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout,
                null, R.string.open,
                R.string.close){}
        mDrawerLayout.addDrawerListener(mDrawerToggle)*/


    }

    fun onAddFabClick(view: View){
        val testToast: Toast = Toast.makeText(this,"FabClicked",Toast.LENGTH_SHORT)
        val bottomSheet=AddNewSheet()
        bottomSheet.show(supportFragmentManager,bottomSheet.tag)
        //testToast.show()
    }

    override fun onStart() {
        super.onStart()
        val currentUser=mAuth.currentUser
        if (currentUser==null){
            gotoLogin()
        }
        val dpURL=mAuth.currentUser?.photoUrl
        val displayPicture= Picasso.with(this).load(dpURL)
        displayPicture.into(user_avatar)
        //Snackbar.make(parent_view,"Welcome "+currentUser?.displayName,Snackbar.LENGTH_LONG).show()
    }
    private fun gotoLogin(){
        startActivity(Intent(this,LoginActivity::class.java))
    }

    private fun signOut(){
        mAuth.signOut()
        startActivity(Intent(this,LoginActivity::class.java))
    }
    lateinit var currentTime:Date
    private fun updateUI(){
        for (keys in reminders.children){
            //Log.d("Output",data.value.toString())
            val data= Reminder(keys.child("name").value.toString(),keys.child("time").value.toString(),keys.child("unit").value.toString())
            remindersList.add(data)
            //Log.d("Output",remindersList.toString())

        }

        currentTime=Calendar.getInstance().time
        val timeInMinutes=currentTime.hours*60+currentTime.minutes
        Log.d("Time:",timeInMinutes.toString())

        remindersList.forEach { it->
            var timeInc=360
            var timeInt: Int
            if (it.unit=="Hours"){
                timeInt=it.time.toInt()*60
            }
            else{
                timeInt=it.time.toInt()
            }
            while (timeInc<=1320){
                var hr=timeInc/60
                var hrString=hr.toString()
                if (hrString.length==1){
                    hrString="0"+hrString
                }

                val minute=timeInc%60
                var minuteString=minute.toString()
                if (minuteString.length==1){
                    minuteString="0"+minuteString
                }

                var ampm="AM"
                if (timeInc>=720){ampm="PM"}

                if(timeInc>=timeInMinutes) {
                    val alarm = Alarm(hrString + ":" + minuteString, ampm, it.name.capitalize())
                    alarmsList.add(alarm)
                }
                timeInc=timeInc+timeInt

            }
            alarmsList.sortBy { it->
                it.time
            }
        }

        Log.d("Alarm:",alarmsList.toString())
        alarmsList.forEach {it->
            list.add(AlarmAdapter.AlarmsData(it.time,it.ampm,it.name))
            //setAlarm(it)
        }
        alarmsListMain.adapter=AlarmAdapter(this@MainActivity,list)
        setAlarm()
    }

    private fun setAlarm(){
        val alarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar=Calendar.getInstance()
        //calendar.timeZone= TimeZone.getTimeZone("IST")
        val cal2=Calendar.getInstance()
        calendar.set(cal2.get(Calendar.YEAR),cal2.get(Calendar.MONTH),cal2.get(Calendar.DATE),7,0,0)
        val initTime=calendar.timeInMillis
        //cal2.timeZone= TimeZone.getTimeZone("IST")
        val currentTime=cal2.timeInMillis
        //val givenDateString = "Tue Apr 23 16:08:28 GMT+05:30";
        Log.d("TimeCheck",initTime.toString()+"::"+currentTime.toString())
        remindersList.forEach {
            var timeInc=initTime
            var timeInt: Long
            val alarmIntent=Intent(this,AlarmReceiver::class.java)
            Log.i("Extra Name",it.name)
            alarmIntent.putExtra("name",it.name)
            val pendingIntent:PendingIntent= PendingIntent.getBroadcast(applicationContext,0,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            if (it.unit=="Hours"){
                timeInt=it.time.toLong()*60*60*1000
                if (currentTime>=initTime) {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, initTime, timeInt,pendingIntent)
                    Toast.makeText(this,"Setting Alarms",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                timeInt=it.time.toLong()*60*1000
                if (currentTime>=initTime) {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, initTime, timeInt,pendingIntent)
                    Toast.makeText(this,"Setting Alarms",Toast.LENGTH_SHORT).show()
                }
            }
        }
        //alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+10000,pendingIntent)

    }

}

private fun DrawerLayout.toggle() {
    if (isDrawerOpen(Gravity.START)){
        closeDrawer(Gravity.START)
    }else{
        openDrawer(Gravity.START)
    }

}


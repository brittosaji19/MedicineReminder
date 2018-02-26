package com.brittosaji.medicinereminder
import android.content.Intent
import android.icu.text.CompactDecimalFormat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_new.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.navigation_main.*

class MainActivity : AppCompatActivity() {



    private lateinit var mAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val appBar:Toolbar=appbar
        //setSupportActionBar(appBar)
        initializeDrawer()

        mAuth= FirebaseAuth.getInstance()
        val logOutButton=logOutButton
        logOutButton.setOnClickListener(View.OnClickListener {
            signOut();
        })

        val list = ArrayList<AlarmAdapter.AlarmsData>()
        list.add(AlarmAdapter.AlarmsData("10:15","PM","Adol"))
        list.add(AlarmAdapter.AlarmsData("11:15","PM","Adol"))
        list.add(AlarmAdapter.AlarmsData("10:10","AM","Adol"))
        list.add(AlarmAdapter.AlarmsData("9:15","PM","Adol"))
        list.add(AlarmAdapter.AlarmsData("10:15","PM","Adol"))
        list.add(AlarmAdapter.AlarmsData("11:15","PM","Adol"))
        list.add(AlarmAdapter.AlarmsData("10:10","AM","Adol"))
        list.add(AlarmAdapter.AlarmsData("9:15","PM","Adol"))
        alarmsListMain.adapter=AlarmAdapter(this,list)



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
        testToast.show()
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

}

private fun DrawerLayout.toggle() {
    if (isDrawerOpen(Gravity.START)){
        closeDrawer(Gravity.START)
    }else{
        openDrawer(Gravity.START)
    }
}


package com.brittosaji.medicinereminder
import android.icu.text.CompactDecimalFormat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_new.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fab=floatingActionButton3
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
    fun onAddFabClick(view: View){
        val testToast: Toast = Toast.makeText(this,"FabClicked",Toast.LENGTH_SHORT)
        val bottomSheet=AddNewSheet()
        bottomSheet.show(supportFragmentManager,bottomSheet.tag)
        testToast.show()
    }
}

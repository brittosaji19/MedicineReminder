package com.brittosaji.medicinereminder

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.alarm_item.view.*
import java.util.ArrayList

/**
 * Created by britto on 18/1/18.
 */
class AlarmAdapter(val activity:Activity,val alarmDatas:ArrayList<AlarmsData>): BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rootView:View=activity.layoutInflater.inflate(R.layout.alarm_item,p2,false)
        rootView.alarmTimeMain.text=alarmDatas[p0].time
        rootView.alarmAmPmMain.text=alarmDatas[p0].amPm
        rootView.alarmNameMain.text=alarmDatas[p0].name
        return rootView
    }

    override fun getItem(p0: Int): Any {
    return alarmDatas[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int =alarmDatas.size

    data class AlarmsData(val time:String,val amPm:String, val name:String)

}
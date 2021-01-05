package com.digisoft.mma.adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.digisoft.mma.R;

import java.util.Calendar;

public class DateTimePickerAdapter implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private int day, month, year, hour, minute;
    private int myday, myMonth, myYear, myHour, myMinute;
    Button button;
    Context context;
    public DateTimePickerAdapter(Context context,Button button){
        this.context = context;
        this.button = button;
    }

    public void pickDateTime(){
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.DialogTheme,this,year, month,day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        Log.e("DAY",day+"");
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.DialogTheme,this, hour, minute, DateFormat.is24HourFormat(context));
        timePickerDialog.show();
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        myMonth++;
        String month = myMonth+"",date = myday+"",minutes = myMinute+"",hours = myHour+"";

        if(myMonth<10){
            month = "0"+myMonth;
        }
        if(myHour<10){
           hours = "0"+myHour;
        }
        if(myMinute<10){
           minutes = "0"+myMinute;
        }
        if(myday<10){
            date = "0"+myday;
        }
        button.setText(myYear+"-"+month+"-"+date+" "+hours+":"+minutes+":00");
    }
}

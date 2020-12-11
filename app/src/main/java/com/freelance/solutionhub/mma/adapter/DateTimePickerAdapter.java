package com.freelance.solutionhub.mma.adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

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
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, this,year, month,day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = day;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, this, hour, minute, DateFormat.is24HourFormat(context));
        timePickerDialog.show();
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        myMonth++;
        if(myMonth<10){
            button.setText(myYear+"-0"+myMonth+"-"+myday+" "+myHour+":"+myMinute+":00");
        }else {
            button.setText(myYear+"-"+myMonth+"-"+myday+" "+myHour+":"+myMinute+":00");
        }
    }
}

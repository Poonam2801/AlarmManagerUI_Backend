package com.example.poonamgupta2801.samaypebhujiya;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.round;


public class MainActivity extends AppCompatActivity {
    private Button alarm_set_alarm;
    private TimePicker alarm_selector;
    private int hour, min, am_pm;
    private int hour_temp;
    private String hour_tempo, min_tempo, am_pm_tempo;
    private RelativeLayout alarm_ui;
    private LinearLayout alarm_set_layout;
    private String FAN_STATE = "FAN_OFF", BULB_STATE = "TL_ON";
    private ImageView alarm_bulb_image;
    private ImageView alarm_fan_image;
    private ImageButton alarm_fan_speed1, alarm_fan_speed2, alarm_fan_speed3, alarm_fan_speed4, alarm_fan_speed5;
    private Calendar current, calendar;
    private String username = null, password = null;
    private String alarm_status;
    private Messenger mService = null;
    private Boolean mBound = false;
    private Boolean isBound = false;
    private String initialGet = null;
    private String session = null;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedpreferences = getApplicationContext().getSharedPreferences("Alarm_List", MODE_PRIVATE);

        initialGet = getStringFromPreferences(null,"ab");

        session = getIntent().getStringExtra("SESSION_ID");


        alarm_ui = (RelativeLayout) findViewById(R.id.alarm_UI);
        alarm_set_alarm = (Button) findViewById(R.id.alarm_set_alarm);
        alarm_selector = (TimePicker) findViewById(R.id.tp);
        alarm_bulb_image = (ImageView) findViewById(R.id.alarm_bulb_image);
        alarm_fan_image = (ImageView) findViewById(R.id.alarm_fan_image);
        alarm_fan_speed1 = (ImageButton) findViewById(R.id.alarm_fan_speed1);
        alarm_fan_speed2 = (ImageButton) findViewById(R.id.alarm_fan_speed2);
        alarm_fan_speed3 = (ImageButton) findViewById(R.id.alarm_fan_speed3);
        alarm_fan_speed4 = (ImageButton) findViewById(R.id.alarm_fan_speed4);
        alarm_fan_speed5 = (ImageButton) findViewById(R.id.alarm_fan_speed5);


       /* if(initialGet!=null){

        }
        else{

        }*/

        changeFanSpeed(0);
        BULB_STATE = "TL_OFF";
        alarm_bulb_image.setImageResource(R.drawable.bulb_off);

        current = new GregorianCalendar();
        hour = current.get(current.HOUR);
        min = current.get(current.MINUTE);
        am_pm = current.get(current.AM_PM);
        long time_current = current.getTimeInMillis();


        alarm_set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Bulb, Fan;
                if (BULB_STATE.equals("TL_OFF")) {
                    Bulb = "Bulb:OFF";
                } else {
                    Bulb = "Bulb:ON";
                }

                if (FAN_STATE.equals("FAN_OFF")) {
                    Fan = "Fan:OFF";
                } else if (FAN_STATE.equals("FAN_ON_1")) {
                    Fan = "FanSpeed:1";
                } else if (FAN_STATE.equals("FAN_ON_2")) {
                    Fan = "FanSpeed:2";
                } else if (FAN_STATE.equals("FAN_ON_3")) {
                    Fan = "FanSpeed:3";
                } else if (FAN_STATE.equals("FAN_ON_4")) {
                    Fan = "FanSpeed:4";
                } else {
                    Fan = "FanSpeed:5";
                }

                if(hour>12){
                    hour_temp=hour-12;
                    am_pm_tempo = "PM";
                }
                else {
                    hour_temp = hour;
                    am_pm_tempo = "AM";
                }
                if(hour_temp<10)hour_tempo = "0"+Integer.toString(hour_temp);
                else hour_tempo = Integer.toString(hour_temp);
                if(min<10)min_tempo = "0"+Integer.toString(min);
                else min_tempo = Integer.toString(min);

                String temp = "Time:" + hour_tempo + ":" + min_tempo+" "+ am_pm_tempo + " | " + Bulb + " | " + Fan;

                if(new String(session).equals("set_alarm")) addNewAlarm(temp);
                else if(new String(session).equals("edit_alarm")) edit_replace_Alarm(initialGet,temp);


                final Calendar alarm = Calendar.getInstance();

                alarm.set(Calendar.HOUR_OF_DAY, alarm_selector.getCurrentHour());
                alarm.set(Calendar.MINUTE, alarm_selector.getCurrentMinute());
                alarm.set(Calendar.SECOND, 0);
                long time1 = current.getTimeInMillis();
                long time2 = alarm.getTimeInMillis();

                Toast.makeText(MainActivity.this, "Setting Alarm...", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();

                intent_trigger();

            }
        });

        alarm_fan_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FAN_STATE == "FAN_OFF") {
                    changeFanSpeed(1);
                } else {
                    changeFanSpeed(0);
                }
            }
        });
        alarm_fan_speed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFanSpeed(1);
            }
        });
        alarm_fan_speed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFanSpeed(2);
            }
        });
        alarm_fan_speed3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFanSpeed(3);
            }
        });
        alarm_fan_speed4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFanSpeed(4);
            }
        });
        alarm_fan_speed5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFanSpeed(5);
            }
        });


        alarm_bulb_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BULB_STATE.equals("TL_ON")) {
                    alarm_bulb_image.setImageResource(R.drawable.bulb_off);
                    BULB_STATE = "TL_OFF";
                } else {
                    alarm_bulb_image.setImageResource(R.drawable.bulb_on);
                    BULB_STATE = "TL_ON";
                }
            }
        });


        alarm_selector.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                min = minute;
            }
        });


    }

    @Override
    public void onBackPressed() {
        intent_trigger();
    }

    private boolean addNewAlarm(String favoriteItem){
        //Get previous favorite items
        String AlarmList = getStringFromPreferences(null,"al");
        // Append new Favorite item
        if(AlarmList!=null){
            AlarmList = AlarmList+","+favoriteItem;
        }else{
            AlarmList = favoriteItem;
        }
        // Save in Shared Preferences
        return putStringInPreferences(AlarmList,"al");
    }

    private boolean putStringInPreferences(String nick,String key){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, nick);
        editor.commit();
        return true;
    }

    private String getStringFromPreferences(String defaultValue, String key){
        if(sharedpreferences.contains(key)) {
            String temp = sharedpreferences.getString(key, defaultValue);
            return temp;
        }
        else return null;
    }

    private boolean edit_replace_Alarm(String data, String replacement){
        //Get previous favorite items
        String favoriteList = getStringFromPreferences(null,"al");
        String newConcat = new String();
        // Append new Favorite item
        if(favoriteList!=null){
            String[] parsed = favoriteList.split(",");
            int size = parsed.length;

            for(int i = 0; i<size; i++){
                if((new String(parsed[i]).equals(data)==false)&&(new String(parsed[i]).equals("")==false)){
                    //if(new String(newConcat).equals("")==true) newConcat = parsed[i];
                    newConcat = newConcat + "," + parsed[i];
                }
                else{
                    if(new String(newConcat).equals("")==true) newConcat = null;
                    else newConcat = newConcat + "," + replacement;
                }
            }
        }
        // Save in Shared Preferences
        return putStringInPreferences(newConcat,"al");
    }

    public void changeFanSpeed(int speed) {
        if (speed == 0) {
            alarm_fan_image.setImageResource(R.drawable.ic_home_fan);

            alarm_fan_speed1.setBackgroundResource(R.color.grey);
            alarm_fan_speed2.setBackgroundResource(R.color.grey);
            alarm_fan_speed3.setBackgroundResource(R.color.grey);
            alarm_fan_speed4.setBackgroundResource(R.color.grey);
            alarm_fan_speed5.setBackgroundResource(R.color.grey);

            FAN_STATE = "FAN_OFF";
        } else if (speed == 1) {
            alarm_fan_image.setImageResource(R.drawable.ic_home_fan1);


            alarm_fan_speed1.setBackgroundResource(R.color.blue);
            alarm_fan_speed2.setBackgroundResource(R.color.grey);
            alarm_fan_speed3.setBackgroundResource(R.color.grey);
            alarm_fan_speed4.setBackgroundResource(R.color.grey);
            alarm_fan_speed5.setBackgroundResource(R.color.grey);

            FAN_STATE = "FAN_ON_1";
        } else if (speed == 2) {
            alarm_fan_image.setImageResource(R.drawable.ic_home_fan2);

            alarm_fan_speed1.setBackgroundResource(R.color.blue);
            alarm_fan_speed2.setBackgroundResource(R.color.blue);
            alarm_fan_speed3.setBackgroundResource(R.color.grey);
            alarm_fan_speed4.setBackgroundResource(R.color.grey);
            alarm_fan_speed5.setBackgroundResource(R.color.grey);

            FAN_STATE = "FAN_ON_2";
        } else if (speed == 3) {
            alarm_fan_image.setImageResource(R.drawable.ic_home_fan3);

            alarm_fan_speed1.setBackgroundResource(R.color.blue);
            alarm_fan_speed2.setBackgroundResource(R.color.blue);
            alarm_fan_speed3.setBackgroundResource(R.color.blue);
            alarm_fan_speed4.setBackgroundResource(R.color.grey);
            alarm_fan_speed5.setBackgroundResource(R.color.grey);

            FAN_STATE = "FAN_ON_3";
        } else if (speed == 4) {
            alarm_fan_image.setImageResource(R.drawable.ic_home_fan4);

            alarm_fan_speed1.setBackgroundResource(R.color.blue);
            alarm_fan_speed2.setBackgroundResource(R.color.blue);
            alarm_fan_speed3.setBackgroundResource(R.color.blue);
            alarm_fan_speed4.setBackgroundResource(R.color.blue);
            alarm_fan_speed5.setBackgroundResource(R.color.grey);

            FAN_STATE = "FAN_ON_4";
        } else if (speed == 5) {
            alarm_fan_image.setImageResource(R.drawable.ic_home_fan5);

            alarm_fan_speed1.setBackgroundResource(R.color.blue);
            alarm_fan_speed2.setBackgroundResource(R.color.blue);
            alarm_fan_speed3.setBackgroundResource(R.color.blue);
            alarm_fan_speed4.setBackgroundResource(R.color.blue);
            alarm_fan_speed5.setBackgroundResource(R.color.blue);

            FAN_STATE = "FAN_ON_5";
        }
    }
    public void intent_trigger(){
        Intent activityIntent=new Intent(getApplicationContext(),Alarm_list_inflator.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getApplicationContext().startActivity(activityIntent);
    }
}


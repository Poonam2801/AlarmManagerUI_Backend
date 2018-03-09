package com.example.poonamgupta2801.samaypebhujiya;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


/**
 * Created by PoonamGupta2801 on 13/02/2018.
 */

public class Alarm_list_inflator extends Activity {
    LinearLayout linearLayout;
    Button set_alarm, delete_button, edit_button;
    private Boolean exit = false;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_alarms);
        linearLayout = (LinearLayout) findViewById(R.id.alarm_list_widget);
        set_alarm = (Button) findViewById(R.id.alarm_set_punnu);

        set_alarm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
                activityIntent.putExtra("SESSION_ID", "set_alarm");
                getApplicationContext().startActivity(activityIntent);
            }
        });

        sharedpreferences = getApplicationContext().getSharedPreferences("Alarm_List", MODE_PRIVATE);

        String[] alarm_list_parts = getAlarmList();
        if (alarm_list_parts != null) {
            int size = alarm_list_parts.length;
            for (int i = 0; i < size; i++) {
                dynamic(alarm_list_parts[i]);
            }
        }
    }

    public void dynamic(final String data) {
        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.list_of_alarmswidget, null);
        linearLayout.addView(addView);

        TextView alarm_set = (TextView) addView.findViewById(R.id.alarm_state);
        alarm_set.setText(data);

        delete_button = (Button) addView.findViewById(R.id.alarm_delete);

        edit_button = (Button) addView.findViewById(R.id.alarm_edit);

        Switch onOffSwitch = (Switch) addView.findViewById(R.id.default_switch);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)Toast.makeText(Alarm_list_inflator.this, "ALARM ON", Toast.LENGTH_SHORT).show();
                else Toast.makeText(Alarm_list_inflator.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
            }

        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) addView.getParent()).removeView(addView);
                removeAlarm(data);
            }
        }
        );

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAlarm(data);
                intent_trigger("ab", data);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
            System.exit(0);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }


    private boolean removeAlarm(String favoriteItem){
        //Get previous favorite items
        String favoriteList = getStringFromPreferences(null,"al");
        String newConcat = new String();
        // Append new Favorite item
        if(favoriteList!=null){
            String[] parsed = favoriteList.split(",");
            int size = parsed.length;

            for(int i = 0; i<size; i++){
                if((new String(parsed[i]).equals(favoriteItem)==false)&&(new String(parsed[i]).equals("")==false)){
                    if(new String(newConcat).equals("")==true) newConcat = parsed[i];
                    else newConcat = newConcat + "," + parsed[i];
                }
            }
        }
        // Save in Shared Preferences
        return putStringInPreferences(newConcat,"al");
    }

    private String[] getAlarmList(){
        String favoriteList = getStringFromPreferences(null,"al");
        return convertStringToArray(favoriteList);
    }
    private boolean putStringInPreferences(String nick,String key){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(new String(nick).equals("")==true)nick = null;
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

    private String[] convertStringToArray(String str){
        if(str!=null) {
            String[] arr = str.split(",");
            return arr;
        }
        else return null;
    }

    public void intent_trigger(String key, String nick){
        Intent activityIntent=new Intent(getApplicationContext(),MainActivity.class);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, nick);
        editor.commit();
        //activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //activityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activityIntent.putExtra("SESSION_ID", "edit_alarm");
        getApplicationContext().startActivity(activityIntent);
    }
}

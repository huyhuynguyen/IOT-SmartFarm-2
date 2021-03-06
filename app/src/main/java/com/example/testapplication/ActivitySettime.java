package com.example.testapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

public class ActivitySettime extends AppCompatActivity {

    private TimePicker timePicker;
    private Button btnSetStart, btnSetEnd, btnDoneSave;
    private TextView timeStartText, timeEndText, deviceTitle;

    private boolean is24hView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settime);

        timePicker = (TimePicker) findViewById(R.id.timePicker2);
        btnSetStart = (Button) findViewById(R.id.btnSet_start);
        btnSetEnd = (Button) findViewById(R.id.btnSet_end);
        btnDoneSave = (Button) findViewById(R.id.btnDoneSave);
        timeStartText = (TextView) findViewById(R.id.timeStart);
        timeEndText = (TextView) findViewById(R.id.timeEnd);
        deviceTitle = (TextView) findViewById(R.id.deviceTime_title);

        timePicker.setIs24HourView(is24hView);

        String deviceName = getIntent().getStringExtra("Device");

        deviceTitle.setText(deviceName + " time");
        timeStartText.setText(getIntent().getStringExtra("Start"));
        timeEndText.setText(getIntent().getStringExtra("End"));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Click to change time start and end
        btnSetStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSetStart.setBackgroundColor(Color.CYAN);
                btnSetEnd.setBackgroundColor(Color.parseColor("#2196F3"));
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        timeStartText.setText(hourOfDay + ":" + minute + ":00");
                    }
                });
            }
        });

        btnSetEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSetEnd.setBackgroundColor(Color.CYAN);
                btnSetStart.setBackgroundColor(Color.parseColor("#2196F3"));
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        timeEndText.setText(hourOfDay + ":" + minute + ":00");
                    }
                });
            }
        });

        // Open main view
        btnDoneSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    LocalDate currentDate = LocalDate.now();
                    String timeStartStr = currentDate + " " + timeStartText.getText();
                    String timeEndStr = currentDate + " " + timeEndText.getText();
                    Date start = (Date)simpleDateFormat.parse(timeStartStr);
                    Date end = (Date) simpleDateFormat.parse(timeEndStr);
                    if (end.compareTo(start) < 0) {
                        showToast();
                    }
                    else {
                        final DocumentReference controlDeviceRef = db.collection("controls").document(deviceName.toLowerCase());
                        turnParentActivity(controlDeviceRef);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*
        Save data and go to parent activity
     */
    public void turnParentActivity(DocumentReference docRef) {
        docRef.update("timeStart", (String) timeStartText.getText());
        docRef.update("timeEnd", (String) timeEndText.getText());
        Intent intent = new Intent(ActivitySettime.this, MainActivityControllDevice.class);
        startActivity(intent);
    }

    public void showToast() {
        Toast toast =  Toast.makeText(ActivitySettime.this, "Wrong end time!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP , 20, 30);
        toast.show();
    }
}
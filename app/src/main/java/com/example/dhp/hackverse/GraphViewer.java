package com.example.dhp.hackverse;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GraphViewer extends AppCompatActivity {
    ArrayList<Date> dates;
    Date date = new Date();
    Calendar cal = Calendar.getInstance();
    private PieChart chart;
    private Button notifyStudent;
    private String MA;
    private String TA;
    private String EmailId;
    private String GroupId;
    private MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_viewer);
        Intent intent = getIntent();
        notifyStudent = findViewById(R.id.notifyStudent);

        String RollNo = intent.getStringExtra("roll_no");
        MA = intent.getStringExtra("ma");
        TA = intent.getStringExtra("ta");
        EmailId = intent.getStringExtra("email_id");
        GroupId = intent.getStringExtra("group_id");
//        Toast.makeText(getApplicationContext(), "This is the data:" + RollNo + ":" + MA + ":" + TA, Toast.LENGTH_SHORT).show();
        setTitle("Roll No: " + RollNo);

        chart = findViewById(R.id.attendanceGraph);


        chart.setBackgroundColor(Color.WHITE);

        moveOffScreen();

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);

        chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);

        chart.setMaxAngle(180f); // HALF CHART
        chart.setRotationAngle(180f);
        chart.setCenterTextOffset(0, -20);

        setData(Integer.valueOf(MA), Integer.valueOf(TA));

        chart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);

        notifyStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });


        calendarView = findViewById(R.id.calendarView);
        setCalendarHighlights(RollNo);

    }

    private void setCalendarHighlights(String RollNo) {
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE); // Removes onClick functionality

        cal.add(Calendar.DATE, 0);

//        calendarView.setDateSelected(CalendarDay.today(), true);
//        calendarView.setDateSelected(CalendarDay.from(2020, 1, 25), true);
//        calendarView.setDateSelected(CalendarDay.from(2020, 1, 26), true);

        AttendanceDbHelper attendanceDbHelper = new AttendanceDbHelper(getApplicationContext());
        SQLiteDatabase db = attendanceDbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + GroupId + " WHERE 0", null);
        try {
            String[] columnNames = c.getColumnNames();
            Log.d("888888888------", columnNames[0]);
            Log.d("888888888------", columnNames[1]);
            Log.d("888888888------", columnNames[2]);
            Log.d("888888888------", columnNames[3]);
            Log.d("888888888------", columnNames[4]);
            Log.d("888888888------", columnNames[5]);

            int outer ;

            for (outer = 5; outer < columnNames.length; outer++) {

                String monthname = "";
                String month0 = "";
                String date = "";
                String year = "";



                for (int i = 0; i < Math.min(columnNames[outer].length(), 3); i++) {
                    monthname += (columnNames[outer].charAt(i));
                }

                if (monthname.equals("jan")) {
                    month0 = "01";
                } else if (monthname.equals("feb")) {
                    month0 = "02";
                } else if (monthname.equals("mar")) {
                    month0 = "03";
                } else if (monthname.equals("apr")) {
                    month0 = "04";
                } else if (monthname.equals("may")) {
                    month0 = "05";
                } else if (monthname.equals("jun")) {
                    month0 = "06";
                } else if (monthname.equals("jul")) {
                    month0 = "07";
                } else if (monthname.equals("aug")) {
                    month0 = "08";
                } else if (monthname.equals("sep")) {
                    month0 = "09";
                } else if (monthname.equals("oct")) {
                    month0 = "10";
                } else if (monthname.equals("nov")) {
                    month0 = "11";
                } else {
                    month0 = "12";
                }
                for (int i = 3; i < Math.min(columnNames[outer].length(), 5); i++) {
                    date += columnNames[outer].charAt(i);
                }
                for (int i = 5; i < Math.min(columnNames[outer].length(), 9); i++) {
                    year += columnNames[outer].charAt(i);
                }

                AttendanceDbHelper attendanceDbHelper1 = new AttendanceDbHelper(getApplicationContext());
                SQLiteDatabase db1 = attendanceDbHelper.getWritableDatabase();
                Cursor c0 = db.rawQuery("SELECT " + columnNames[outer] + " FROM " + GroupId + " WHERE rollNumber=" + RollNo, null);
                c0.moveToFirst();
                int status = c0.getInt(0);
                if (status == 1) {
                    calendarView.setDateSelected(CalendarDay.from(Integer.valueOf(year), Integer.valueOf(month0), Integer.valueOf(date)), true);
                }


            }
//            jan262020
//            Log.d("888888888------",columnNames[6]);
//            Log.d("888888888------",columnNames[7]);

        } finally {
            c.close();
        }


    }


    private void sendMail() {
        String toMail = EmailId;
        Float percentage = Float.valueOf(MA) / Float.valueOf(TA) * 100;
        String message = "This is your current attendance status : " + percentage;
        if (percentage < 30.00) {
            message = message + "\n WARNING! Your Attendance is low\n";
        }
        message = message + "\n\n\nAttendance Management Team\nBy BBB\n";

        String subject = "Attendance Status";

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, toMail, subject, message);

        javaMailAPI.execute();

    }


    private void setData(int part, int total) {

        ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(part));
        values.add(new PieEntry(total - part));


        PieDataSet dataSet = new PieDataSet(values, "Attendance Status");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        chart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("The Greener The Better");
        return s;
    }

    private void moveOffScreen() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;

        int offset = (int) (height * 0.00); /* percent to move */

        LinearLayout.LayoutParams llParams =
                (LinearLayout.LayoutParams) chart.getLayoutParams();
        llParams.setMargins(0, 0, 0, -offset);
        chart.setLayoutParams(llParams);
    }


}

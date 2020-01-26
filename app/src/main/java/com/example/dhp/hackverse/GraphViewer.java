package com.example.dhp.hackverse;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
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

import java.util.ArrayList;

public class GraphViewer extends AppCompatActivity {
    private PieChart chart;
    private Button notifyStudent;
    private String MA;
    private String TA;
    private String EmailId;


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

    }


    private void sendMail() {
        String toMail = EmailId;
        Float percentage = Float.valueOf(MA) / Float.valueOf(TA) * 100;
        String message = "This is your current attendance status : " + percentage;
        if (percentage<30.00){
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

        int offset = (int) (height * 0.65); /* percent to move */

        LinearLayout.LayoutParams llParams =
                (LinearLayout.LayoutParams) chart.getLayoutParams();
        llParams.setMargins(0, 0, 0, -offset);
        chart.setLayoutParams(llParams);
    }


}

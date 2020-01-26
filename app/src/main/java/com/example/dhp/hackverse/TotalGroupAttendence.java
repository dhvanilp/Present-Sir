package com.example.dhp.hackverse;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class TotalGroupAttendence extends AppCompatActivity {

    EditText groupid;
    String rollnumber[];
    String ma[];
    String ta[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_group_attendence);

        groupid = findViewById(R.id.groupid);
        Button submit = findViewById(R.id.submitbutton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = groupid.getText().toString().trim();

                AttendanceDbHelper dbHelper = new AttendanceDbHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();


                Cursor cx = db.rawQuery("SELECT count(name) FROM sqlite_master where type = 'table'", null);
                cx.moveToFirst();
                int counted = cx.getInt(0);
                Log.v("Counted", counted + "");
                String[] presentgroups = new String[counted - 1];

                Cursor cy = db.rawQuery("SELECT name FROM sqlite_master where type = 'table'", null);
                cy.moveToFirst();
                int i = 0;
                while (!cy.isAfterLast()) {
                    if (!cy.getString(0).equals("android_metadata")) {
                        presentgroups[i] = cy.getString(0).trim();
                        Log.v("name", cy.getString(0));
                        i++;
                    }
                    cy.moveToNext();
                }
                int flag = 0;
                for (int u = 0; u < presentgroups.length; u++) {
                    if (name.equals(presentgroups[u])) {
                        flag = 1;
                    }
                }

                if (flag == 0) {
                    groupid.setError("No such group record present");
                    groupid.requestFocus();
                    return;
                }

                String query = "select rollNumber,markedAttendence,totalAttendence from " + name;

                String query2 = "select count(*) from " + name;
                Cursor c1 = db.rawQuery(query2, null);
                c1.moveToFirst();
                int count = c1.getInt(0);
                Log.v("countoftotalstudents", count + "");
                rollnumber = new String[count];
                ma = new String[count];
                ta = new String[count];
                Cursor c = db.rawQuery(query, null);
                c.moveToFirst();
                int w = 0;
                while (!c.isAfterLast()) {
                    rollnumber[w] = c.getString(0);
                    ma[w] = c.getInt(1) + "";
                    ta[w] = c.getInt(2) + "";
                    w++;
                    Log.v("Rollnumber", c.getString(0));
                    c.moveToNext();
                }

                // roll number

                ListView rollnumber2 = findViewById(R.id.roll_listview);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.total_attendence_textview, R.id.typetext, rollnumber);
                rollnumber2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        Intent intent = new Intent(getApplicationContext(), GraphViewer.class);
                        intent.putExtra("roll_no", rollnumber[position]);
                        intent.putExtra("ma", ma[position]);
                        intent.putExtra("ta", ta[position]);
                        String groupId = groupid.getText().toString().trim();
                        intent.putExtra("email_id",getEmailId(groupId,rollnumber[position]));
                        intent.putExtra("group_id",groupid.getText().toString().trim());
                        startActivity(intent);
                    }
                });
                rollnumber2.setAdapter(arrayAdapter);


                ListView markedattendence = findViewById(R.id.ma_listview);
                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.total_attendence_textview, R.id.typetext, ma);
                markedattendence.setAdapter(arrayAdapter2);

                ListView totalattendence = findViewById(R.id.ta_listview);
                ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(getApplicationContext(), R.layout.total_attendence_textview, R.id.typetext, ta);
                totalattendence.setAdapter(arrayAdapter3);

            }
        });


    }

    private String getEmailId(String groupID, String rollNumber) {
        AttendanceDbHelper dbHelper = new AttendanceDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT emailAddress from " + groupID + " WHERE rollNumber=" + rollNumber;
        Cursor cx = db.rawQuery(query, null);
        cx.moveToFirst();
        String email = cx.getString(0);
        Log.v("This is the email:", email);
        return email;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), UserAccount.class);
        startActivity(i);
    }
}
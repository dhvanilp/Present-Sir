package com.example.dhp.hackverse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.microsoft.projectoxford.face.FaceServiceClient;

public class MainActivity extends AppCompatActivity {
    static String printTag = "This is a print tag::::";
//    static final String SERVER_HOST = "https://southeastasia.api.cognitive.microsoft.com/face/v1.0";
    static final String SERVER_HOST = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0";
    static final String SUBSCRIPTION_KEY = "3d675aba8a9c49b4b80ca8b11bfab8c3";
    private static FaceServiceClient faceServiceClient;
    ListView simpleList;
    private String[] a, b;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        boolean state = isNetworkAvailable();
        if (state) {
            final Button createperson = (Button) findViewById(R.id.createPersongroup);
            createperson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), CreateGroup.class);
                    startActivity(i);
                }
            });

            Button ownaccount = findViewById(R.id.your_account);
            ownaccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), UserAccount.class);
                    startActivity(i);
                }
            });
            AttendanceDbHelper helper = new AttendanceDbHelper(getApplicationContext());
            SQLiteDatabase dbs = helper.getReadableDatabase();
            Cursor c = dbs.rawQuery("SELECT count(name) FROM sqlite_master where type = 'table'", null);
            c.moveToFirst();
            int counted = c.getInt(0);
            Log.d(MainActivity.printTag,String.valueOf(counted));
            Log.v("Counted", counted + "");
            a = new String[counted - 1];
            b = new String[counted - 1];

            Cursor c1 = dbs.rawQuery("SELECT name FROM sqlite_master where type = 'table'", null);
            c1.moveToFirst();
            int i = 0;
            while (!c1.isAfterLast()) {
                if (!c1.getString(0).equals("android_metadata")) {
                    a[i] = (i + 1) + ".) " + c1.getString(0);
                    b[i] = c1.getString(0).trim();
                    Log.v("name", c1.getString(0));
                    i++;
                }
                c1.moveToNext();
            }
            dbs.close();
            Log.v("table names from", "called");

            simpleList = findViewById(R.id.simpleListView);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview, R.id.textView, a);
            simpleList.setAdapter(arrayAdapter);

            simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    Intent intent = new Intent(getApplicationContext(), PersonGroup.class);
                    String msg = b[position];
                    Log.e("passedstring", msg);
                    intent.putExtra("groupId", msg);
                    startActivity(intent);
                }
            });

//
        } else {
            new android.app.AlertDialog.Builder(this).setTitle("No internet")
                    .setMessage("Check your internet connection and try again!")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}

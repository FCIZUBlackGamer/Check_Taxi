package com.example.mahmoudalsadany.checktaxi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Start_tawsila extends AppCompatActivity {

    Button start, stop, alarm;
    EditText Field_from, Field_to;
    String get_email, get_from, get_to, get_car_id, get_driver_image, get_driver_id, start_ride_time;
    Intent intent;
    ImageView driver_image;
    TextView car_id;
    Database database;
    Cursor cursor;
    LastWay_Database lastWay_database;

    public Handler handler = null;
    public static Runnable runnable = null;
    EditText editText;
    String passwrod;
    boolean state = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tawsila);

        database = new Database(this);
        lastWay_database = new LastWay_Database(this);
        cursor = database.ShowData();
        while (cursor.moveToNext()) {
            get_email = cursor.getString(1);
            passwrod = cursor.getString(2);
        }
        editText = new EditText(this);
        editText.setHint("Please Enter Password");
        editText.setBackgroundColor(getColor(R.color.white));
        editText.setTextColor(getColor(R.color.black));
        editText.setHintTextColor(getColor(R.color.black));

        intent = getIntent();
        get_driver_image = intent.getStringExtra("driver_photo_url");
        get_car_id = intent.getStringExtra("car_id");
        get_driver_id = intent.getStringExtra("driver_id");

        driver_image = (ImageView) findViewById(R.id.driver_image);
        car_id = (TextView) findViewById(R.id.car_id);
        start = (Button) findViewById(R.id.start_ride);
        stop = (Button) findViewById(R.id.stop);
        alarm = (Button) findViewById(R.id.alarm);
        Field_from = (EditText) findViewById(R.id.From);
        Field_to = (EditText) findViewById(R.id.To);
        stop.setEnabled(false);
        alarm.setEnabled(false);
        start.setEnabled(true);


        if (intent.getStringExtra("VALUE") != null) {
            get_email = intent.getStringExtra("email");
            get_driver_image = intent.getStringExtra("driver_photo_url");
            get_driver_id = intent.getStringExtra("driver_id");
            Field_from.setText(intent.getStringExtra("place_from"));
            Field_to.setText(intent.getStringExtra("destination"));
            car_id.setText(intent.getStringExtra("car_id"));
            stop.setEnabled(true);
            alarm.setEnabled(true);
            start.setEnabled(false);

//            do {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Confirm")
//                        .setMessage("This is a Confirmation Message")
//                        .setView(editText)
//                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (passwrod.equals(editText.getText().toString())) {
//                                    state = true;
//                                }
//                            }
//                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).show();
//            } while (!state);

        }
        Picasso.with(this)
                .load(get_driver_image)
                .into(driver_image);

        database = new Database(this);
//        lastWay_database = new LastWay_Database(this);
        cursor = database.ShowData();
        while (cursor.moveToNext()) {
            get_email = cursor.getString(1);
        }
        car_id.setText(get_car_id);

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                cursor = database.ShowData();
                while (cursor.moveToNext()) {
                    start_ride_time = cursor.getString(4);
//                    Toast.makeText(Start_tawsila.this, start_ride_time, Toast.LENGTH_SHORT).show();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(Start_tawsila.this);
                builder.setTitle("Confirm")
                        .setMessage("This is a Confirmation Message")
                        .setView(editText)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (passwrod.equals(editText.getText().toString())) {
                                    state = true;
//                                        final ProgressDialog progressDialog = new ProgressDialog(Start_tawsila.this);
//                                        progressDialog.setMessage("Loading Data ...");
//                                        progressDialog.show();
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://check-taxi.000webhostapp.com/EndTawsila.php",
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    if (v != null) {
                                                        ViewGroup parent = (ViewGroup) v.getParent();
                                                        if (parent != null) {
                                                            parent.removeAllViews();
                                                        }
                                                    }
                                                    Toast.makeText(Start_tawsila.this, response, Toast.LENGTH_SHORT).show();
//                                                        progressDialog.dismiss();
                                                    if (response.equals("Thanks To Allah That You Are Fine <3")) {
                                                        stopService(new Intent(Start_tawsila.this, AlertService.class));
                                                        stop.setEnabled(false);
                                                        alarm.setEnabled(false);
                                                        Intent intent = new Intent(Start_tawsila.this, MapsActivity.class);
                                                        intent.putExtra("Key", "stop");
                                                        startActivity(intent);
//                                                        android.os.Process.killProcess(android.os.Process.myPid());
//                                                        System.exit(1);
                                                    }
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
//                                                progressDialog.dismiss();
                                            if (error instanceof ServerError)
                                                Toast.makeText(Start_tawsila.this, "Server Error", Toast.LENGTH_SHORT).show();
                                            else if (error instanceof NetworkError)
                                                Toast.makeText(Start_tawsila.this, "Bad Network", Toast.LENGTH_SHORT).show();
                                            else if (error instanceof TimeoutError)
                                                Toast.makeText(Start_tawsila.this, "Timeout Error", Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(Start_tawsila.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            HashMap hashMap = new HashMap();
                                            hashMap.put("start_time", start_ride_time);
                                            return hashMap;
                                        }
                                    };
                                    Volley.newRequestQueue(Start_tawsila.this).add(stringRequest);

                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

//                    ((ViewGroup)editText.getParent()).removeView(editText);
//                        if (v != null) {
//                            ViewGroup parent = (ViewGroup) editText.getParent();
//                            if (parent != null) {
//                                parent.removeAllViews();
//                            }
//                        }


            }
        });
        alarm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                // Waiting For Action
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "122"));
                startActivity(intent);
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_from = Field_from.getText().toString();
                get_to = Field_to.getText().toString();
                lastWay_database.DropTable();

                final ProgressDialog progressDialog = new ProgressDialog(Start_tawsila.this);
                progressDialog.setMessage("Loading Data ...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://check-taxi.000webhostapp.com/InsertNewTawsila.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(Start_tawsila.this, "Start Ride @ " + response, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                if (response.equals("OOops something is wrong ")) {

                                } else {
                                    start_ride_time = response;
                                    database.UpdateData("1", start_ride_time);
//                                    Intent intent = new Intent( Start_tawsila.this, AlertService.class );
//                                    startService( intent );
                                    handler = new Handler();
                                    runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            handler.postDelayed(runnable, 300000 /*5*60*10000*/); // 5 Minuit
                                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Start_tawsila.this);
                                            mBuilder.setSmallIcon(R.mipmap.taxi);

                                            mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
                                            mBuilder.setContentTitle("Confirm Message!");

                                            mBuilder.setContentText("Hi, Have You Finished Your Ride?!");
//                                            mBuilder.addAction(R.drawable.ic_prev, "BUTTON 1", myIntentToButtonOneScreen) // #0
//                                                    .addAction(R.drawable.ic_pause, "BUTTON 2", myIntentToButtonTwoScreen)
                                            Intent resultIntent = new Intent(Start_tawsila.this, Start_tawsila.class);
                                            resultIntent.putExtra("VALUE", "OK");
                                            resultIntent.putExtra("car_id", get_car_id);
                                            resultIntent.putExtra("email", get_email);
                                            resultIntent.putExtra("driver_photo_url", get_driver_image);
                                            resultIntent.putExtra("driver_id", get_driver_id);
                                            resultIntent.putExtra("place_from", get_from);
                                            resultIntent.putExtra("destination", get_to);
                                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(Start_tawsila.this);
                                            stackBuilder.addParentStack(Start_tawsila.class);

                                            // Adds the Intent that starts the Activity to the top of the stack
                                            stackBuilder.addNextIntent(resultIntent);
                                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                            mBuilder.setContentIntent(resultPendingIntent);

                                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            Random random = new Random();
                                            int notificationID = random.nextInt(300000);
                                            // notificationID allows you to update the notification later on.
                                            mNotificationManager.notify(notificationID, mBuilder.build());
                                        }
                                    };
                                    handler.postDelayed(runnable, 300000);

                                    stop.setEnabled(true);
                                    alarm.setEnabled(true);
                                    start.setEnabled(false);
                                    Intent intent = new Intent(Start_tawsila.this, MapsActivity.class);
                                    intent.putExtra("Key", "Start");
                                    startActivity(intent);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Start_tawsila.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap hashMap = new HashMap();
                        hashMap.put("car_id", get_car_id + "");
                        hashMap.put("email", get_email + "");
                        hashMap.put("driver_id", get_driver_id + "");
                        hashMap.put("place_from", get_from + "");
                        hashMap.put("destination", get_to + "");
                        return hashMap;
                    }
                };
//                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
//                        0,  // maxNumRetries = 0 means no retry
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(Start_tawsila.this).add(stringRequest);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            while (cursor.moveToNext()) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                database.UpdateData("1", " ", " ", "0");
                startActivity(intent);
            }
        } else if (item.getItemId() == R.id.action_about) {
            startActivity(new Intent(Start_tawsila.this, AboutusActivity.class));
        } else if (item.getItemId() == R.id.last_map) {
            Intent intent = new Intent(Start_tawsila.this, MapsActivity.class);
            intent.putExtra("Key", "Map");
            startActivity(intent);
        }
        return true;
    }
}

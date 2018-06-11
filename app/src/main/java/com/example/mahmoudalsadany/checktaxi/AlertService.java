package com.example.mahmoudalsadany.checktaxi;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AlertService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    Database database;
    Cursor cursor;
    String get_start_time;

    public AlertService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Ride Starts!", Toast.LENGTH_LONG).show();
        database = new Database( context );
        cursor = database.ShowData();
        while (cursor.moveToNext()){
            get_start_time = cursor.getString( 1 );
        }
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Toast.makeText(context, "Ride is still running", Toast.LENGTH_LONG).show();
                handler.postDelayed(runnable,300000 /*5*60*10000*/); // 5 Minuit

                AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(context, R.style.myDialog);


                builder.setTitle("Confirm Message!")
                        .setMessage("Have You Finished Your Ride?!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                final ProgressDialog progressDialog = new ProgressDialog(context);
                                progressDialog.setMessage("Loading Data ...");
                                progressDialog.show();
                                StringRequest stringRequest = new StringRequest( Request.Method.POST, "https://check-taxi.000webhostapp.com/EndTawsila.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                if (response.equals( "Thanks To Allah That You Are Fine <3" )) {
                                                    stopSelf();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context,error.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("start_time",get_start_time);
                                        return hashMap;
                                    }
                                };
                                Volley.newRequestQueue(context).add(stringRequest);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);
                     AlertDialog alert = builder.create();

                    alert.getWindow().setType( WindowManager.LayoutParams.TYPE_SYSTEM_ALERT );
                    alert.show();
            }
        };
        handler.postDelayed(runnable, 15000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(context, "On Start Command", Toast.LENGTH_LONG).show();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        Toast.makeText(this, "Ride Finished", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Ride Started By Customer.", Toast.LENGTH_LONG).show();
    }
}

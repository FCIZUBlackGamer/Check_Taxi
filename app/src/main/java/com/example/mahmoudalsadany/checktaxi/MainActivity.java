package com.example.mahmoudalsadany.checktaxi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText email ,password;
    String emaillog ,passwordlog;
    Button login;
    CheckBox remember;
    TextView signup;
    Database database;
    Cursor cursor;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new Database( this );
        cursor = database.ShowData();

        cursor = database.ShowData();
        Log.e("NUMBER",cursor.getCount()+"");
        if (cursor.getCount()==0)
        {
            database.InsertData(" "," ", "0");
            Log.e("INSERT","ROW INSERTED");

        }else if (cursor.getCount()>=1){
            while (cursor.moveToNext()) {
                Log.e("GO","ROW FOUND"+ cursor.getString( 1 )+cursor.getString( 2)+cursor.getString( 3 ));
                if (cursor.getString( 2 ).equals("1")) {
                    Log.e("GO","ROW FOUND");
                    String email = cursor.getString( 1 );
                    Log.e("GO","ROW FOUND "+ email);
                    intent = new Intent( MainActivity.this,AvilableActivity.class );
                    startActivity( intent );
                }
            }
        }
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        login = (Button)findViewById( R.id.login );
        remember = (CheckBox)findViewById( R.id.checkBox );
        signup = (TextView)findViewById( R.id.sign_up );
        login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emaillog=email.getText().toString();
                passwordlog=password.getText().toString();
                //Script
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading Data ...");
                progressDialog.show();
                StringRequest stringRequest = new StringRequest( Request.Method.POST, "https://check-taxi.000webhostapp.com/Login.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                if (response.equals( "Welcome Home!" )){
                                    //Insert into sqlite
                                    if (remember.isChecked()){
                                        database.UpdateData( "1", email.getText().toString(),passwordlog , "1" );
                                    }else {
                                        database.UpdateData( "1", email.getText().toString(),passwordlog , "0" );
                                    }
                                    //Start Activity
                                    Intent login=new Intent(MainActivity.this,AvilableActivity.class);
                                    startActivity(login);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap hashMap = new HashMap();
                        hashMap.put("password",passwordlog);
                        hashMap.put("email",emaillog);
                        return hashMap;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        0,  // maxNumRetries = 0 means no retry
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(MainActivity.this).add(stringRequest);
            }
        } );

        signup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(register);
            }
        } );

    }
}

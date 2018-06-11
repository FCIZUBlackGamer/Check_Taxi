package com.example.mahmoudalsadany.checktaxi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {
    EditText id,name,email,password, c_pass, phone, address;
    String userId,userName,userEmail,userPassword, con_pass, user_phone, user_address;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        id=(EditText)findViewById(R.id.userid);
        name=(EditText)findViewById(R.id.username);
        email=(EditText)findViewById(R.id.useremail);
        password=(EditText)findViewById(R.id.userpassword);
        c_pass=(EditText)findViewById(R.id.c_pass);
        phone=(EditText)findViewById(R.id.phone);
        address=(EditText)findViewById(R.id.Address);
        register = (Button)findViewById( R.id.register );
        register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId=id.getText().toString();
                userName=name.getText().toString();
                userEmail=email.getText().toString();
                userPassword=password.getText().toString();
                con_pass=c_pass.getText().toString();
                user_phone=phone.getText().toString();
                user_address=address.getText().toString();
                final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Loading Data ...");
                progressDialog.show();
                StringRequest stringRequest = new StringRequest( Request.Method.POST, "https://check-taxi.000webhostapp.com/UserRegister.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this,response,Toast.LENGTH_SHORT).show();
                                if (response.equals( " User Add Successfully " )){
                                    Intent Lint =new Intent(RegisterActivity.this,MainActivity.class);
                                    startActivity(Lint);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap hashMap = new HashMap();
                        hashMap.put("id",userId);
                        hashMap.put("email",userEmail);
                        hashMap.put("password",userPassword);
                        hashMap.put("name",userName);
                        hashMap.put("phone",user_phone);
                        hashMap.put("address",user_address);
                        return hashMap;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        0,  // maxNumRetries = 0 means no retry
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(RegisterActivity.this).add(stringRequest);
            }
        } );
    }
}

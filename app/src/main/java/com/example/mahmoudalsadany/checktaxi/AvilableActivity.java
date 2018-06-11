package com.example.mahmoudalsadany.checktaxi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class AvilableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ROW_ITEM> item;
    EditText search_field;
    Button search;
    String search_word;
    Database database;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_avilable );

        database = new Database( this );
        cursor = database.ShowData();
        search_field = (EditText) findViewById( R.id.search_car_num );
        search = (Button) findViewById( R.id.get_car );
        search.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_word = search_field.getText().toString();
                if (!search_word.equals( "" ))
                    LoadRecyclerViewSearchItems( search_word );
            }
        } );
        recyclerView = (RecyclerView) findViewById( R.id.list_car );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        item = new ArrayList<>();
        if (isNetworkConnected()) {
            LoadRecyclerViewData();
        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder( this, android.R.style.Theme_Material_Dialog_Alert );
            } else {
                builder = new AlertDialog.Builder( this );
            }
            builder.setTitle( "Error Message!" )
                    .setMessage( "Make Sure You Are Connected To Wifi!" )
                    .setPositiveButton( android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    } )
                    .setNegativeButton( android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    } )
                    .setIcon( android.R.drawable.ic_dialog_alert )
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu, menu );

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            while (cursor.moveToNext()) {
                Intent intent = new Intent( this, MainActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                database.UpdateData( "1", " "," ", "0" );
                startActivity( intent );
            }
        } else if (item.getItemId() == R.id.action_about) {
            startActivity( new Intent( AvilableActivity.this, AboutusActivity.class ) );
        }
        return true;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );

        return cm.getActiveNetworkInfo() != null;
    }

    private void LoadRecyclerViewData() {

        final int size = item.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                item.remove( 0 );
            }
            adapter.notifyItemRangeRemoved( 0, size );
        }
        final ProgressDialog progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( "Loading Data ..." );
        progressDialog.show();
        StringRequest stringRequest = new StringRequest( Request.Method.POST, "https://check-taxi.000webhostapp.com/GetCarsInfo.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String s = URLEncoder.encode( response, "ISO-8859-1" );
                            response = URLDecoder.decode( s, "UTF-8" );
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            JSONArray jsonArray = jsonObject.getJSONArray( "cars_data" );
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject( i );
                                ROW_ITEM iteme = new ROW_ITEM(
                                        object.getString( "model_car" ),
                                        object.getString( "car_id" ),
                                        object.getString( "car_color" )
                                );
                                item.add( iteme );
                            }
                            adapter = new ROW_ITEM_ADAPTER( item, AvilableActivity.this );
                            recyclerView.setAdapter( adapter );

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof ServerError)
                    Toast.makeText( AvilableActivity.this, "Server Error", Toast.LENGTH_SHORT ).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText( AvilableActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT ).show();
                else if (error instanceof NetworkError)
                    Toast.makeText( AvilableActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT ).show();

            }
        } );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,  // maxNumRetries = 0 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue( this ).add( stringRequest );
    }

    private void LoadRecyclerViewSearchItems(final String word) {
        final int size = item.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                item.remove( 0 );
            }
            adapter.notifyItemRangeRemoved( 0, size );
        }
        final ProgressDialog progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( "Loading Data ..." );
        progressDialog.show();
        StringRequest stringRequest = new StringRequest( Request.Method.POST, "https://check-taxi.000webhostapp.com/GetOneCarInfo.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String s = URLEncoder.encode( response, "ISO-8859-1" );
                            response = URLDecoder.decode( s, "UTF-8" );
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                        try {
                            if (response.isEmpty()) {
                                Toast.makeText( AvilableActivity.this, "No Items", Toast.LENGTH_SHORT ).show();
                            } else {
                                JSONObject jsonObject = new JSONObject( response );
                                JSONArray jsonArray = jsonObject.getJSONArray( "cars_data" );
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject( i );
                                    ROW_ITEM iteme = new ROW_ITEM(
                                            object.getString( "model_car" ),
                                            object.getString( "car_id" ),
                                            object.getString( "car_color" )
                                    );
                                    item.add( iteme );
                                }
                                adapter = new ROW_ITEM_ADAPTER( item, AvilableActivity.this );
                                recyclerView.setAdapter( adapter );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText( AvilableActivity.this, error.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap hashMap = new HashMap();
                hashMap.put( "car_id", word );
                return hashMap;
            }
        };
        Volley.newRequestQueue( this ).add( stringRequest );
    }
}

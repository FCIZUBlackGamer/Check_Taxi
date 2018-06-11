package com.example.mahmoudalsadany.checktaxi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class AboutusActivity extends AppCompatActivity {

    Database database;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        database = new Database( this );
        cursor = database.ShowData();
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
        if (item.getItemId()== R.id.action_logout){
            while (cursor.moveToNext()) {
                Intent intent = new Intent( this, MainActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                database.UpdateData( "1", " "," ", "0" );
                startActivity( intent );
            }
        }else if (item.getItemId()== R.id.action_about){
            startActivity( new Intent( AboutusActivity.this,AboutusActivity.class ) );
        }else if (item.getItemId()== R.id.last_map){
            Intent intent = new Intent(AboutusActivity.this,MapsActivity.class);
            intent.putExtra("Key","Map");
            startActivity(intent);
        }
        return true;
    }
}

package com.example.mahmoudalsadany.checktaxi;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mahmoudalsadany on 03/02/18.
 */

public class DRIVER_ITEM_ADAPTER extends RecyclerView.Adapter<DRIVER_ITEM_ADAPTER.Holder> {
    List<ROW_ITEM> row_items;
    Context context;
    Intent intent;
    String car_id;

    public DRIVER_ITEM_ADAPTER(List<ROW_ITEM> row_items, Context context, String car_id) {
        this.row_items = row_items;
        this.context = context;
        this.car_id = car_id;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_driver_row,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final ROW_ITEM noti_item = row_items.get(position);

        holder.name.setText(noti_item.getDriver_name());
        holder.id.setText(noti_item.getDriver_id());
        Picasso.with(context)
                .load(noti_item.getDriver_image_url())
                .into(holder.driver_image);

        holder.ride.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Confirm Message!")
                        .setMessage("Are You Going to Ride With This Driver!?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                intent = new Intent( context,Start_tawsila.class );
                                intent.putExtra( "driver_photo_url",noti_item.getDriver_image_url() );
                                intent.putExtra( "car_id",car_id );
                                intent.putExtra( "driver_id",noti_item.getDriver_id() );
                                context.startActivity( intent );
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } );
    }

    @Override
    public int getItemCount() {
        return row_items.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView name, id;
        ImageView driver_image;
        Button ride;
        public Holder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.driver_name);
            id = (TextView)itemView.findViewById(R.id.driver_id);
            driver_image = (ImageView)itemView.findViewById(R.id.driver_image);
            ride = (Button)itemView.findViewById( R.id.take_ride );
        }
    }
}

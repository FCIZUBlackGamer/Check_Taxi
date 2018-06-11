package com.example.mahmoudalsadany.checktaxi;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mahmoudalsadany on 03/02/18.
 */

public class ROW_ITEM_ADAPTER extends RecyclerView.Adapter<ROW_ITEM_ADAPTER.Holder> {
    List<ROW_ITEM> row_items;
    Context context;

    public ROW_ITEM_ADAPTER(List<ROW_ITEM> row_items, Context context) {
        this.row_items = row_items;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car_row,parent,false);

        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final ROW_ITEM noti_item = row_items.get(position);

        holder.name.setText(noti_item.getName());
        holder.name.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
            Intent intent = new Intent( context,DriverResult.class );
            intent.putExtra( "car_id", noti_item.getNum());
            context.startActivity( intent );
            }
        } );
        holder.num.setText(noti_item.getNum());
        Picasso.with(context)
                .load(noti_item.getImage_url())
                .into(holder.car_icon);
    }

    @Override
    public int getItemCount() {
        return row_items.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView name, num;
        ImageView car_icon;
        public Holder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.car_name);
            num = (TextView)itemView.findViewById(R.id.car_num);
            car_icon = (ImageView)itemView.findViewById(R.id.image_car_logo);
        }
    }
}

package com.example.mahmoudalsadany.checktaxi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matt on 5/12/2015.
 */
public class DeviceListAdapter extends ArrayAdapter<DeviceItem>{

    private Context context;
    private BluetoothAdapter bTAdapter;
    Intent intent;
    ProgressDialog progressDialog;

    public DeviceListAdapter(Context context, List items, BluetoothAdapter bTAdapter) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.bTAdapter = bTAdapter;
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        TextView titleText;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View line = null;
        final DeviceItem item = (DeviceItem)getItem(position);
        final String name = item.getDeviceName();
        TextView macAddress = null;
        View viewToUse = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        viewToUse = mInflater.inflate(R.layout.device_list_item, null);
        holder = new ViewHolder();
        holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
        viewToUse.setTag(holder);

        macAddress = (TextView)viewToUse.findViewById(R.id.macAddress);
        line = (View)viewToUse.findViewById(R.id.line);
        holder.titleText.setText(item.getDeviceName());
        macAddress.setText(item.getAddress());
        holder.titleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait ...");
                progressDialog.setCancelable(false);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://check-taxi.000webhostapp.com/GetDriversInfo.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    progressDialog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("cars_data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        intent = new Intent( context,Start_tawsila.class );
                                        intent.putExtra( "driver_photo_url",object.getString("driver_photo") );
                                        intent.putExtra( "car_id",item.getDeviceName() );
                                        intent.putExtra( "driver_id",object.getString("car_driver_national_id") );
                                        context.startActivity( intent );

                                    }
                                }catch (JSONException e){
                                    progressDialog.dismiss();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap hashMap = new HashMap();
                        hashMap.put("car_id", item.getDeviceName());
                        return hashMap;
                    }
                };
                Volley.newRequestQueue(context).add(stringRequest);
            }
        });

        if ( item.getDeviceName().toString() == "No Devices") {
            macAddress.setVisibility(View.INVISIBLE);
            line.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    ((int) RelativeLayout.LayoutParams.WRAP_CONTENT, (int) RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            holder.titleText.setLayoutParams(params);
        }

        return viewToUse;
    }


}

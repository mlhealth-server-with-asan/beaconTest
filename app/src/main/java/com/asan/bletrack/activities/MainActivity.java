package com.asan.bletrack.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asan.bletrack.Ping;
import com.asan.bletrack.R;
import com.asan.bletrack.SettingsLoader;
import com.asan.bletrack.StaticResources;
import com.asan.bletrack.WatchForegroundService;


import com.asan.bletrack.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private TextView deviceidText;
    private ActivityMainBinding binding;
    private Intent foregroundService;
    private ImageView setting;
    private ImageView network;
    private ImageView server;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticResources.maincontext = getApplicationContext();
        StaticResources.pref = new SettingsLoader();
        StaticResources.pref.getsettings();

        UIBind();

//        foregroundService = new Intent(this, WatchForegroundService.class);
//        foregroundService.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//        foregroundService.setData(Uri.parse("package:" + getPackageName()));

        String deviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        StaticResources.deviceID = deviceid;
        getWatchid(deviceid);
        deviceidText.setText(deviceid);
    }



    @Override
    protected void onResume() {
        super.onResume();
        network_check();
        server_check();
    }

    protected void UIBind(){
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deviceidText = binding.deviceid;
        setting = binding.setting;
        network = binding.networkicon;
        server = binding.servericon;

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pwintent = new Intent(MainActivity.this, PasswordActivity.class);
                startActivity(pwintent);
            }
        });
    }

    protected void network_check(){
        Ping ping = new Ping("https://dns.google/");
        ping.start();
        try{
            ping.join();
            if(ping.isSuccess()){
                network.setImageResource(R.drawable.baseline_check_24);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void server_check(){
        Ping ping = new Ping(StaticResources.ServerURL);
        ping.start();
        try{
            ping.join();
            if(ping.isSuccess()){
                server.setImageResource(R.drawable.baseline_check_24);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void getWatchid(String deviceId) {

        String URL = StaticResources.ServerURL + "api/watch/"+deviceId;
        JSONObject json_object = new JSONObject();
        try {
            json_object.put("androidId", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendNetworkGetRequest(URL,json_object,deviceId);
    }

    protected void sendNetworkGetRequest(String URL, JSONObject jsonData,String androidId) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String mRequestBody = jsonData.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);

                int state = jsonResponse.getInt("status");
                if (state == 200) {
                    String watchId = jsonResponse.getJSONObject("data").getString("watchId");
                    Log.d("WatchId", "Received watchId: " + watchId);

                    Intent intent = new Intent(getApplicationContext(), WatchForegroundService.class);
                    intent.putExtra("watchId", watchId);
                    startService(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                new android.os.Handler().postDelayed(() -> getWatchid(androidId), 5000);
            }
        }, error -> {
            Log.e("NetworkError", error.toString());
            registerWatch(androidId);
            new android.os.Handler().postDelayed(() -> getWatchid(androidId), 5000);
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return mRequestBody.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = new String(response.data, StandardCharsets.UTF_8);
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);
    }

    private void registerWatch(String deviceId) {
        Log.e("TLqkf",deviceId);
        String URL = StaticResources.ServerURL + "api/watch";
        JSONObject json_object = new JSONObject();
        try {
            json_object.put("uuid", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendNetworkPostRequest(URL,json_object);
    }



    protected void sendNetworkPostRequest(String URL, JSONObject jsonData) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String mRequestBody = jsonData.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);

                int state = jsonResponse.getInt("status");
                if (state == 200) {
                    String watchId = jsonResponse.getJSONObject("data").getString("watchId");
                    Log.d("WatchId", "Received watchId: " + watchId);

                    Intent intent = new Intent(getApplicationContext(), WatchForegroundService.class);
                    intent.putExtra("watchId", watchId);
                    startService(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("NetworkError", error.toString());
        }) {



            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return mRequestBody.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = new String(response.data, StandardCharsets.UTF_8);
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);
    }


}
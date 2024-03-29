package com.asan.bletrack.activities;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.ConcatAdapter;

import com.asan.bletrack.Ping;
import com.asan.bletrack.R;
import com.asan.bletrack.SettingsLoader;
import com.asan.bletrack.StaticResources;
import com.asan.bletrack.WatchForegroundService;


import com.asan.bletrack.databinding.ActivityMainBinding;
import com.asan.bletrack.dto.DeviceInfo;

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

        foregroundService = new Intent(this, WatchForegroundService.class);
        foregroundService.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        foregroundService.setData(Uri.parse("package:" + getPackageName()));
        startForegroundService(foregroundService);

        String deviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        StaticResources.deviceID = deviceid;
        StaticResources.device = Build.MODEL;
        StaticResources.os = Build.VERSION.RELEASE;
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
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceID(StaticResources.deviceID);
        deviceInfo.setDevice(StaticResources.device);
        deviceInfo.setOs(StaticResources.os);
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
}
package com.asan.bletrack.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.asan.bletrack.StaticResources;
import com.asan.bletrack.databinding.ActivitySettingBinding;

public class SettingActivity extends Activity {
    private EditText URL;
    private EditText pw;
    private Button save;
    private ActivitySettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIBind();
        URL.setText(StaticResources.ServerURL);
        pw.setText(Integer.toString(StaticResources.password));
    }

    protected void UIBind(){
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        save = binding.save;
        URL = binding.URL;
        pw = binding.password;

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticResources.pref.putsettings(URL.getText().toString(), Integer.parseInt(pw.getText().toString()));
                StaticResources.pref.getsettings();
            }
        });

    }
}

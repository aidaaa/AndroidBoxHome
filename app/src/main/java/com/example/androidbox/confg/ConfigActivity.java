package com.example.androidbox.confg;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidbox.R;
import com.example.androidbox.databinding.ActivityConfigBinding;
import com.example.androidbox.main.MainActivity;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelConfig viewModelConfig =new ViewModelConfig(this);
        ActivityConfigBinding binding= DataBindingUtil.setContentView(this, R.layout.activity_config);
        binding.setConfig(viewModelConfig);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(ConfigActivity.this, MainActivity.class);
        startActivity(intent);
    }

}

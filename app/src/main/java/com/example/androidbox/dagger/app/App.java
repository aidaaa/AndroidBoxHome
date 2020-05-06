package com.example.androidbox.dagger.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidbox.dagger.AndroidModule;
import com.example.androidbox.dagger.PlayerComponent;
import com.example.androidbox.dagger.PlayerModule;

/*import com.example.myapplication.dagger.AndroidModule;
import com.example.myapplication.dagger.DaggerPlayerComponent;
import com.example.myapplication.dagger.PlayerComponent;
import com.example.myapplication.dagger.PlayerModule;*/


public class App extends Application
{
    PlayerComponent playerComponent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static App getApp()
    {
        return new App();
    }

 /*   public PlayerComponent getDaggerComponent(AppCompatActivity appCompatActivity)
    {
        playerComponent= DaggerPlayerComponent.builder()
                .androidModule(new AndroidModule(appCompatActivity.getApplicationContext()))
                .playerModule(new PlayerModule())
                .build();
        return playerComponent;
    }*/
}

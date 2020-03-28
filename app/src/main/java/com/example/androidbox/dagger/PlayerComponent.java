package com.example.androidbox.dagger;



import com.example.androidbox.main.MainActivity;

import dagger.Component;

@Component(modules = {AndroidModule.class,PlayerModule.class})
public interface PlayerComponent
{
    void getPlayer(MainActivity mainActivity);
}

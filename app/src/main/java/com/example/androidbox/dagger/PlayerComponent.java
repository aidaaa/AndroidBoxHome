package com.example.androidbox.dagger;



import android.content.Context;
import android.content.SharedPreferences;

import com.example.androidbox.main.MainActivity;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;


@Component(modules = {PlayerModule.class,AndroidModule.class})
public interface PlayerComponent
{
    void getPlayer(MainActivity mainActivity);

    @Component.Builder
    interface Builder
    {
        @BindsInstance
        Builder context(Context context);

       /* @BindsInstance
        Builder shared(SharedPreferences preferences);*/

        PlayerComponent build();
    }
}

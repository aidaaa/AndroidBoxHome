package com.example.androidbox.dagger;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

public class ContextClass
{
    public Context context;

    @Inject
    public ContextClass(Context context) {
        this.context = context;
    }
}

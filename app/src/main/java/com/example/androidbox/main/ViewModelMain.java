package com.example.androidbox.main;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;


public class ViewModelMain extends BaseObservable
{
    private String channelNum;

    @Bindable
    public String getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(String channelNum) {
        this.channelNum = channelNum;
        //notifyPropertyChanged(com.example.myapplication.BR.channelNum);
    }

}

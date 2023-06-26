package com.example.osolmvcandroid.mvc;

import android.app.Application;
import android.content.Context;
/*
 based on https://stackoverflow.com/a/22371510
 Purpose : to get context from any where
*/
public class MySuperAppApplication extends Application {
    private static Application instance;

    // Variables for storage/retrieval of Activity/Fragment Datas
    private String currentPertinantURL;
    // Variables for storage/retrieval of Activity/Fragment Datas ends here

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static MySuperAppApplication getInstance()
    {
        return (MySuperAppApplication)instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }


    //Methods for storage/retrieval of Activity/Fragment Datas
    public void setCurrentPertinantURL(String currentPertinantURL)
    {
        this.currentPertinantURL = currentPertinantURL;
    }

    public String getCurrentPertinantURL()
    {
        return currentPertinantURL;
    }
    //Methods for storage/retrieval of Activity/Fragment Datas ends here
}

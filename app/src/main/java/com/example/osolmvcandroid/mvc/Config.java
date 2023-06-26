package com.example.osolmvcandroid.mvc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Config {
    private   Context currentContext;
    private static Config inst;
    private SharedPreferences wmbPreference;

    private String homePage = "";



    public static Config getInstance()
    {

        if(inst == null)
        {
            inst = new Config();
        }
        return inst;
    }
    private Config()
    {
        currentContext = MySuperAppApplication.getContext();
        wmbPreference = PreferenceManager.getDefaultSharedPreferences(currentContext);
        //Log.d(TAG, "OnInstallHelper: "+"update upkar_favourite_links set date_added = '"+dbModel.getInstance(context).getDateTime()+"'");
    }
    public  Context getApplicationContext() {
        return currentContext;
    }

    public String getHomePage()
    {
        return homePage;
    }



}


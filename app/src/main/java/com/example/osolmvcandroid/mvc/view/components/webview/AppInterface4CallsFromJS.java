package com.example.osolmvcandroid.mvc.view.components.webview;



import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.osolmvcandroid.FirstFragment;
import com.example.osolmvcandroid.mvc.Config;


public class AppInterface4CallsFromJS {
    private Config config;
    private Context applicationContext;
    private Fragment calledFromFragment;// for calling specific fragment related Function(if called from fragment)

    private AppCompatActivity currentActivity;

    private String TAG = "AppInterface4CallsFromJS";

    public AppInterface4CallsFromJS( AppCompatActivity appCompatActivity)
    {
        config = Config.getInstance();
        applicationContext = config.getApplicationContext();
        currentActivity = appCompatActivity;
    }
    public void setCalledFromFragment(Fragment calledFromFragment)
    {
        this.calledFromFragment = calledFromFragment;
    }

    @JavascriptInterface
    public void testCallFrmJS()
    {
        Log.d(TAG, "testCallFrmJS: called "+ "at line no:"+Thread.currentThread().getStackTrace()[2].getLineNumber()+ " of  "+ Thread.currentThread().getStackTrace()[2].getFileName());
    }
    @JavascriptInterface
    public void osolmvcFacebookLogin()
    {
        if(calledFromFragment instanceof FirstFragment) // add safety check if required
        {
            currentActivity.runOnUiThread(() -> ((FirstFragment) calledFromFragment).launchFacebookLoginActivity());
        }
    }
    @JavascriptInterface
    public void osolmvcGoogleLogin()
    {


        //launch googleloginActivity via startActivityForResult
        if(calledFromFragment instanceof FirstFragment) // add safety check if required
        {

               /*
                *  navigation transition should run on 'runOnUiThread'(the main thread)
                * otherwise throws exception java.lang.IllegalStateException: Method setCurrentState must be called on the main thread
                //https://issuetracker.google.com/issues/171125856
                *
                    2023-03-22 12:35:49.348  2024-2270  System.err              com.example.osolmvcandroid           W  	at androidx.lifecycle.LifecycleRegistry.enforceMainThreadIfNeeded(LifecycleRegistry.java:323)
                    2023-03-22 12:35:49.348  2024-2270  System.err              com.example.osolmvcandroid           W  	at androidx.lifecycle.LifecycleRegistry.setCurrentState(LifecycleRegistry.java:120)
                    2023-03-22 12:35:49.348  2024-2270  System.err              com.example.osolmvcandroid           W  	at androidx.navigation.NavBackStackEntry.updateState(NavBackStackEntry.kt:175)
                    2023-03-22 12:35:49.348  2024-2270  System.err              com.example.osolmvcandroid           W  	at androidx.navigation.NavBackStackEntry.setMaxLifecycle(NavBackStackEntry.kt:150)
                    2023-03-22 12:35:49.349  2024-2270  System.err              com.example.osolmvcandroid           W  	at androidx.navigation.NavController.updateBackStackLifecycle$navigation_runtime_release(NavController.kt:977)
                    2023-03-22 12:35:49.349  2024-2270  System.err              com.example.osolmvcandroid           W  	at androidx.navigation.NavController.dispatchOnDestinationChanged(NavController.kt:892)
                    2023-03-22 12:35:49.349  2024-2270  System.err              com.example.osolmvcandroid           W  	at androidx.navigation.NavController.navigate(NavController.kt:1726)
                    2023-03-22 12:35:49.349  2024-2270  System.err              com.example.osolmvcandroid           W  	at androidx.navigation.NavController.navigate(NavController.kt:1541)
                    2023-03-22 12:35:49.349  2024-2270  System.err              com.example.osolmvcandroid           W  	at androidx.navigation.NavController.navigate(NavController.kt:1468)
                    2023-03-22 12:35:49.349  2024-2270  System.err              com.example.osolmvcandroid           W  	at androidx.navigation.NavController.navigate(NavController.kt:1450)
                    2023-03-22 12:35:49.349  2024-2270  System.err              com.example.osolmvcandroid           W  	at androidx.navigation.NavController.navigate(NavController.kt:1433)
                    2023-03-22 12:35:49.350  2024-2270  System.err              com.example.osolmvcandroid           W  	at com.example.osolmvcandroid.FirstFragment.launchGoogleLoginActivity(FirstFragment.java:183)
                    2023-03-22 12:35:49.350  2024-2270  System.err              com.example.osolmvcandroid           W  	at com.example.osolmvcandroid.mvc.view.components.webview.AppInterface4CallsFromJS.osolmvcGoogleLogin(AppInterface4CallsFromJS.java:52)
                    2023-03-22 12:35:49.350  2024-2270  System.err              com.example.osolmvcandroid           W  	at android.os.MessageQueue.nativePollOnce(Native Method)
                    2023-03-22 12:35:49.350  2024-2270  System.err              com.example.osolmvcandroid           W  	at android.os.MessageQueue.next(MessageQueue.java:331)
                    2023-03-22 12:35:49.350  2024-2270  System.err              com.example.osolmvcandroid           W  	at android.os.Looper.loop(Looper.java:149)
                    2023-03-22 12:35:49.350  2024-2270  System.err              com.example.osolmvcandroid           W  	at android.os.HandlerThread.run(HandlerThread.java:65)
                * */
               // throw(new Exception("LaunchGoogleLoginActivity",new Throwable("JS call from web frontend")));
                // kotlin solution https://stackoverflow.com/a/65663536 , run it inside runOnUiThread
                // example https://www.tutorialkart.com/kotlin-android/android-runonuithread-example-kotlin/
                // java https://stackoverflow.com/a/69457408
                //java full example https://www.tabnine.com/code/java/methods/android.app.Activity/runOnUiThread
                currentActivity.runOnUiThread(() -> ((FirstFragment) calledFromFragment).launchGoogleLoginActivity());



        }
    }
    @JavascriptInterface
    public void osolmvcGoogleLogout()
    {
        Log.i(TAG,"osolmvcGoogleLogout() called" );
        if(calledFromFragment instanceof FirstFragment)
        {
            currentActivity.runOnUiThread(() -> ((FirstFragment) calledFromFragment).launchGoogleLogoutActivity());
        }
    }

    @JavascriptInterface
    public void osolmvcFacebookLogout()
    {
        Log.i(TAG,"osolmvcFacebookLogout() called" );
        if(calledFromFragment instanceof FirstFragment)
        {
            currentActivity.runOnUiThread(() -> ((FirstFragment) calledFromFragment).launchFacebookLogoutActivity());
        }
    }

    @JavascriptInterface
    public void osolmvcGoogleLoginFailed()
    {
        Log.i(TAG,"osolmvcGoogleLoginFailed() called" );
        if(calledFromFragment instanceof FirstFragment)
        {
            currentActivity.runOnUiThread(() -> ((FirstFragment) calledFromFragment).launchGoogleReLoginActivity());
        }
    }
    @JavascriptInterface
    public void osolmvcFacebookLoginFailed()
    {
        Log.i(TAG,"osolmvcFacebookLoginFailed() called" );
        if(calledFromFragment instanceof FirstFragment)
        {
            currentActivity.runOnUiThread(() -> ((FirstFragment) calledFromFragment).launchFacebookReLoginActivity());
        }
    }
}

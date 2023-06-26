package com.example.osolmvcandroid.mvc.view.components.webview;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.osolmvcandroid.FirstFragment;
import com.example.osolmvcandroid.mvc.Config;
import com.google.android.material.snackbar.Snackbar;

import java.util.regex.Pattern;

public class CustWebViewClient extends WebViewClient {
    private Config config;
    public AppCompatActivity currentActivity;

    private String TAG = "CustWebViewClient";
    String currentURL;
    Fragment calledFromFragment;

    WebViewHelper currentWebViewHelper;
    public void setCalledFromFragment(Fragment calledFromFragment) {
        this.calledFromFragment = calledFromFragment;
    }
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError er) {
        handler.proceed();
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        EditText editText = (EditText) this.currentActivity.findViewById(com.example.osolmvcandroid.R.id.txtWebAddress);
        String webAddress2Go = url;
        currentURL = url;
        Log.d("editText ", "is " + editText);
        Log.d("homeURL set", url);


        if (editText != null) editText.setText(webAddress2Go);
        String loadedURLType = getLoadedURLType(currentURL);
        Log.d("loadedURLType", currentURL + " " + loadedURLType + getClass().getName() + " " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        String purposeOfActivity = ((FirstFragment) calledFromFragment).getPurposeofActivity();
        Log.d(TAG, "purposeOfActivity is " + purposeOfActivity + getClass().getName() + " " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        switch (loadedURLType) {
            case "LOGIN":
                //https://stackoverflow.com/questions/51570906/how-to-add-a-javascript-function-in-webview-and-call-it-later-from-html-upon-sub
                /*currentWebViewHelper.verifyGoogleIDToken();
                currentWebViewHelper.setGoogleIdToken(null);*/
                    if (purposeOfActivity != null &&
                            (purposeOfActivity.equals("RETURN_AFTER_GOOGLE_LOGIN") ||
                             purposeOfActivity.equals("RETURN_AFTER_FACEBOOK_LOGIN") )) {
                        currentWebViewHelper.verifyToken();
                    }
                    break;

                    case "PROFILE":
                        if (purposeOfActivity != null && purposeOfActivity.equals("RETURN_AFTER_GOOGLE_LOGOUT")) {
                            Log.d(TAG, " calling redirectAfterAndroidLogout " + getClass().getName() + " " + Thread.currentThread().getStackTrace()[2].getLineNumber());
                            currentWebViewHelper.redirectAfterAndroidLogout();
                        }
                        break;


                }
                super.onPageFinished(view, url);


        }


    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        final Uri uri = Uri.parse(url);
        return handleUri(uri);
    }
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        final Uri uri = request.getUrl();
        return handleUri(uri);
    }

    public void setWebViewHelper(WebViewHelper currentWebViewHelper)
    {
        this.currentWebViewHelper = currentWebViewHelper;
    }


    //https://stackoverflow.com/a/38484061
    private boolean handleUri(final Uri uri) {
        Log.i(TAG, "Uri =" + uri);
        final String host = uri.getHost();
        final String scheme = uri.getScheme();
        final String url = uri.toString();
        // Based on some condition you need to determine if you are going to load the url
        // in your web view itself or in a browser.
        // You can use `host` or `scheme` or any part of the `uri` to decide.
        if( (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) && url.contains("whatsapp://chat"))// for sending message  https://api.whatsapp.com/send?text=some text
        {
                /*Intent whatsappIntent = new Intent();
                whatsappIntent.setAction(Intent.ACTION_VIEW);
                whatsappIntent.setPackage("com.example.osolmvcandroid");
                startActivity(whatsappIntent);*/
            //https://stackoverflow.com/questions/3872063/how-to-launch-an-activity-from-another-application-in-android
            Intent launchIntent = null;

            launchIntent = currentActivity.getPackageManager().getLaunchIntentForPackage("com.example.osolmvcandroid");

            if (launchIntent != null) {
                currentActivity.startActivity(launchIntent);//null pointer check in case package name was not found
            }
            return true;
        }



        if( URLUtil.isNetworkUrl(url) ) {// if it is http url , ignore
            return false;
        }
        Snackbar.make(currentActivity.getWindow().getDecorView().findViewById(android.R.id.content)/*.getRootView()*/, "URL is  " + url ,Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        if (appInstalledOrNot(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            currentActivity.startActivity( intent );
        } else {


            {
                // do something if app is not installed
                /*Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content)*//*.getRootView()*//*, R.string.app_not_installed,Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();*/
            }
        }
        return true;

    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = currentActivity.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
    public int Boolean2Int(boolean isOn) {
        int trueOrFalse;
        if (isOn == true) {
            trueOrFalse = 1;
        } else {
            trueOrFalse = 0;
        }
        return trueOrFalse;
    }
    private String getLoadedURLType(String loadedURL)
    {
        String loadedURLType = "NOT_DETECTEABLE";
        if(Pattern.compile(".*Account/androidLogin.*").matcher(loadedURL).matches())
        {
            loadedURLType = "LOGIN";
        }
        if(Pattern.compile(".*Account/profile.*").matcher(loadedURL).matches())
        {
            loadedURLType = "PROFILE";
        }
        if(Pattern.compile("Account/logout").matcher(loadedURL).matches())
        {
            loadedURLType = "LOGOUT";
        }
        return loadedURLType;
    }

}

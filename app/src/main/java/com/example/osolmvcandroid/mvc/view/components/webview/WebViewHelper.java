package com.example.osolmvcandroid.mvc.view.components.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.osolmvcandroid.mvc.Config;

public class WebViewHelper {
    private AppCompatActivity currentActivity;
    private static WebViewHelper inst;
    private SharedPreferences wmbPreference;
    private Bundle savedInstanceStateReceived;

    private WebView myWebView;

    private Config mvcConfig;

    private String homePageURL;

    private String TAG = "WebViewHelper";

    private AppInterface4CallsFromJS wappInterface;
    private WebSettings webSettings;
    private CustWebViewClient mwwClient;// get on page finished,shouldOverrideUrlLoading etc
    private CustWebChromeClient custWebChromeClient;// gives console message, full screen video,onJsAlert etc

    private Fragment calledFromFragment;// for calling specific fragment related Function(if called from fragment)
    private String googleIdToken;
    private String facebookAccessToken;
    private String loginType;


    /* public static WebViewHelper getInstance(AppCompatActivity context)
     {
 
         if(inst == null)
         {
             inst = new WebViewHelper(context);
         }
         return inst;
     }*/
    public WebViewHelper(AppCompatActivity context, WebView webViewInst)
    {
        currentActivity = context;
        mvcConfig = Config.getInstance();
        myWebView = webViewInst;
                //wmbPreference = PreferenceManager.getDefaultSharedPreferences(currentActivity);
        homePageURL = mvcConfig.getHomePage();
    }
    public void setCalledFromFragment(Fragment calledFromFragment)
    {
        this.calledFromFragment = calledFromFragment;
    }
    @SuppressLint("JavascriptInterface")
    public void setWebView(/*Bundle savedInstanceState*/) {
        //savedInstanceStateReceived = savedInstanceState;

        if(wappInterface == null) {
            wappInterface = new AppInterface4CallsFromJS(currentActivity);
            mwwClient = new CustWebViewClient();
            mwwClient.setCalledFromFragment(calledFromFragment);
            mwwClient.currentActivity = currentActivity;
            mwwClient.setWebViewHelper(this);
            custWebChromeClient = new CustWebChromeClient(currentActivity);
            if (calledFromFragment != null) {
                wappInterface.setCalledFromFragment(calledFromFragment);
            }

            Log.i(TAG, "myWebView is " + myWebView);
            myWebView.clearCache(true);
            myWebView.clearHistory();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // for using inspect tab in chrome://inspect/devices#devices for debugging
                myWebView.setWebContentsDebuggingEnabled(true);
            }
            webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);


            webSettings.setAllowContentAccess(true);
            //webSettings.setAppCacheEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setUseWideViewPort(true);

            webSettings.setBuiltInZoomControls(true);

            myWebView.requestFocusFromTouch();

            myWebView.addJavascriptInterface(wappInterface, "OSOLMVCAndroidJSInterface");


            //mwwClient.mContext = this;
            myWebView.setWebViewClient(mwwClient);

            myWebView.setWebChromeClient(custWebChromeClient);
            //webView.setDownloadListener : to download files like browser
            myWebView.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent,
                                            String contentDisposition, String mimetype,
                                            long contentLength) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    currentActivity.startActivity(i);
                }
            });
        }//if(wappInterface == null) {
        //loadHomePage();
    }//public void setWebView(/*Bundle savedInstanceState*/) {

    public void loadHomePage() {
        myWebView.loadUrl(homePageURL);
    }
    public void refresh() {
        String currentURL =  getCurrentURL();
        Log.i(TAG,"reloading currentURL " + currentURL);
        myWebView.loadUrl(currentURL);
    }
    public void loadURL(String givenURL)
    {
        myWebView.loadUrl(givenURL);
    }

    public String getCurrentURL()
    {
        /*EditText editText = (EditText) this.currentActivity.findViewById(com.example.osolmvcandroid.R.id.txtWebAddress);
        String currentURL =  editText.getText().toString();*/
        String currentURL =  myWebView.getUrl();
        return currentURL;
    }


    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
    public void setFacebookAccessToken(String facebookAccessTokenReceived) {
        this.facebookAccessToken = facebookAccessTokenReceived;
    }

    public void setGoogleIdToken(String googleIdTokenReceived) {
        this.googleIdToken = googleIdTokenReceived;
    }

    public String getGoogleIdToken()
    {
        return this.googleIdToken;
    }
    public void verifyToken()
    {
        Log.d(TAG,"loginType is  " + loginType + " " + getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
        switch(loginType)
        {
            case "GOOGLE":
                verifyGoogleIDToken();
                setGoogleIdToken(null);
                break;
            case "FACEBOOK":
                verifyFacebookAccessToken();
                setFacebookAccessToken(null);
                break;
        }
    }

    private void verifyFacebookAccessToken() {
        if(facebookAccessToken != null)
        {
            //showGoogleIdToken
            String js2Evaluate = "(function() { "+
                    "return verifyFacebookAccessToken('"+facebookAccessToken+"');\n"+
                    "})();";

            Log.d(TAG, " js2Evaluate in verifyFacebookAccessToken is "+js2Evaluate);
            myWebView.evaluateJavascript(js2Evaluate,
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String returnedFromHtml) {
                            Log.d(TAG, "returnedFromHtml for facebookAccessToken is "+returnedFromHtml);

                        }
                    });
        }
    }

    public void verifyGoogleIDToken() {
        if(googleIdToken != null)
        {
            //showGoogleIdToken
            String js2Evaluate = "(function() { "+
                    "return verifyGoogleIdToken('"+googleIdToken+"');\n"+
                    "})();";

            Log.d(TAG, " js2Evaluate in verifyGoogleIDToken is "+js2Evaluate);
            myWebView.evaluateJavascript(js2Evaluate,
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String returnedFromHtml) {
                            Log.d(TAG, "returnedFromHtml for verifyGoogleIDToken is "+returnedFromHtml);

                        }
                    });
        }
    }
    public void redirectAfterAndroidLogout(){
        //if(googleIdToken != null)
        {
            //showGoogleIdToken
            String js2Evaluate = "(function() { "+
                    "return redirectAfterAndroidLogout();\n"+
                    "})();";

            Log.d(TAG, " js2Evaluate in redirectAfterAndroidLogout is "+js2Evaluate);
            myWebView.evaluateJavascript(js2Evaluate,
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String returnedFromHtml) {
                            Log.d(TAG, "returnedFromHtml for verifyGoogleIDToken is "+returnedFromHtml);

                        }
                    });
        }
    }
}

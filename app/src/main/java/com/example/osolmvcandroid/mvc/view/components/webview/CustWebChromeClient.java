package com.example.osolmvcandroid.mvc.view.components.webview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class CustWebChromeClient extends WebChromeClient{

    private AppCompatActivity currentActivity;
    private String TAG = "CustWebChromeClient";
    public CustWebChromeClient(AppCompatActivity appCompatActivity)
    {
        super();
        currentActivity = appCompatActivity;
    }
    @Override
    public boolean onConsoleMessage(ConsoleMessage cm) {
        Log.d(TAG/*" MyApplication"*/, " JS Console.log, at line no:"+Thread.currentThread().getStackTrace()[2].getLineNumber()+ " of  "+ Thread.currentThread().getStackTrace()[2].getFileName()+ " , " +cm.message() + "  -- From line "
                + cm.lineNumber() + " of "
                + cm.sourceId() );
        return true;
    }
    /* public void onConsoleMessage(String message, int lineNumber, String sourceID) {
         Log.d("MyApplication", message + " -- From line "
                 + lineNumber + " of "
                 + sourceID);
     }*/
    /*
    # onJsAlert
    Notify the host application that the web page wants to display a JavaScript alert() dialog.

    The default behavior if this method returns false or is not overridden is to show a dialog containing
    the alert message and suspend JavaScript execution until the dialog is dismissed.

    To show a custom dialog, the app should return true from this method(eg: https://stackoverflow.com/a/16239000), in which case the default dialog will
    not be shown and JavaScript execution will be suspended. The app should call JsResult.confirm() when the custom dialog
    is dismissed such that JavaScript execution can be resumed.
    * */
    //https://developer.android.com/reference/android/webkit/WebChromeClient#onJsAlert(android.webkit.WebView,%20java.lang.String,%20java.lang.String,%20android.webkit.JsResult)
    //https://stackoverflow.com/a/16239000
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        //Required functionality here
        return super.onJsAlert(view, url, message, result);
    }



    /*code block for full screen in webview
     based on https://www.monstertechnocodes.com/2018/07/how-to-enable-fullscreen-mode-in-any-html/*/
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mFullscreenContainer;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;



    public Bitmap getDefaultVideoPoster()
    {
        if (mCustomView == null) {
            return null;
        }
        return BitmapFactory.decodeResource(currentActivity.getApplicationContext().getResources(), 2130837573);
    }

    public void onHideCustomView()
    {
        ((FrameLayout)currentActivity.getWindow().getDecorView()).removeView(this.mCustomView);
        this.mCustomView = null;
        currentActivity.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
        currentActivity.setRequestedOrientation(this.mOriginalOrientation);
        this.mCustomViewCallback.onCustomViewHidden();
        this.mCustomViewCallback = null;
    }

    public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
    {
        if (this.mCustomView != null)
        {
            onHideCustomView();
            return;
        }
        this.mCustomView = paramView;
        this.mOriginalSystemUiVisibility = currentActivity.getWindow().getDecorView().getSystemUiVisibility();
        this.mOriginalOrientation = currentActivity.getRequestedOrientation();
        this.mCustomViewCallback = paramCustomViewCallback;
        ((FrameLayout)currentActivity.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
        currentActivity.getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
    //code block for full screen in webview ends here
}

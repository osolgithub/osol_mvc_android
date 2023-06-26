package com.example.osolmvcandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.osolmvcandroid.mvc.MySuperAppApplication;
import com.example.osolmvcandroid.mvc.view.components.webview.WebViewHelper;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.example.osolmvcandroid.databinding.FragmentFirstBinding;

import org.jetbrains.annotations.Nullable;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private WebViewHelper myWebViewHelper;

    private String TAG = "FirstFragment";
    private String LAST_VISITED_URL = "currentPertinantURL";
    String currentPertinantURL;

    private  String purposeOfActivity  = "";

    private Integer webViewLoadCount = 0;

    GoogleSignInClient mGoogleSignInClient = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        /**
         PLANNED WORK FLOW
         1. WebView in first fragment
             1. Server side page for android google login
             2. on click google login call graph `action action_FirstFragment_to_SecondFragment`
         */


        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);*/
                go2SecondFragment(null);

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.i(TAG, "Fragment destroyed");
    }

    // Life cycle methods https://developer.android.com/guide/fragments/lifecycle



    @Override
    public void onStart() {
        super.onStart();
        setPurposeofActivity();
        Log.i(TAG, "On Start called");
        /*if(purposeOfActivity.equals("GOOGLE_LOGOUT_FROM_APP"))
        {
            //replyLogout2WebViewPage();
        }*/
        if(webViewLoadCount == 0) {
            setWebView(/*savedInstanceState*/);
            Log.i(TAG, "setWebView called , webViewLoadCount is " + webViewLoadCount);
        }
        else {
            Log.i(TAG, "setWebView **NOT** called , webViewLoadCount is " + webViewLoadCount);
        }
        webViewLoadCount++;
    }
    @Override
    public void onResume() {

        super.onResume();
        Log.i(TAG, "On Resume called");

    }
    //https://stackoverflow.com/a/15935826 how onSaveInstanceState works
    @Override
    public void onPause() {
        super.onPause();
        currentPertinantURL = myWebViewHelper.getCurrentURL();
        MySuperAppApplication.getInstance().setCurrentPertinantURL(currentPertinantURL);
        Log.d(TAG, "currentPertinantURL is saved . in onPause : " + currentPertinantURL);

    }

    //https://developer.android.com/guide/fragments/saving-state
    /*onSaveInstanceState is designed to save the state of the Activity/Fragment in the case that the OS needs to destroy it for memory reasons or configuration changes*/
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(myWebViewHelper != null) {
            currentPertinantURL = myWebViewHelper.getCurrentURL();
            MySuperAppApplication.getInstance().setCurrentPertinantURL(currentPertinantURL);
            Log.d(TAG, "currentPertinantURL is saved. " + currentPertinantURL);
            //outState.putBoolean(IS_EDITING_KEY, isEditing);
            outState.putString(LAST_VISITED_URL, currentPertinantURL);
        }
    }
    //https://developer.android.com/guide/fragments/saving-state
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentPertinantURL = savedInstanceState.getString(LAST_VISITED_URL);
            Log.d(TAG, "currentPertinantURL is retrieved. " + currentPertinantURL);
        } else {
            //https://developer.android.com/topic/libraries/architecture/viewmodel
            //https://developer.android.com/guide/fragments/saving-state#nonconfig
            //randomGoodDeed = viewModel.generateRandomGoodDeed();
            currentPertinantURL = MySuperAppApplication.getInstance().getCurrentPertinantURL();
            Log.d(TAG, "currentPertinantURL is retrieved. from MySuperAppApplication " + currentPertinantURL);
        }
    }




    // CUSTOM METHODS
    private void setPurposeofActivity()
    {
        // get the text from FirstFragment
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            purposeOfActivity = bundle.getString("PURPOSE_OF_CALL");//GOOGLE_LOGIN_TO_WEBSITE or RE_GOOGLE_LOGIN_TO_WEBSITE
        }
        else
        {
            purposeOfActivity = "INDEPENDENT_FRAGMENT";
        }
    }
    public String  getPurposeofActivity() {
        return purposeOfActivity;
    }
    private void setWebView(/*Bundle savedInstanceState*/)
    {

        /*
        For Adding WebView https://developer.android.com/develop/ui/views/layout/webapps/webview
        http://www.outsource-online.net/blog/2023/03/08/android-app-development-quick-tips/#webview
        1. Add WebView in layout
        2. Add permissions in AndroidManifest.xml
        3. Set WebSettings for enabling javascript operations
        4. Add WebAppInterface4CallsFromJS (custom object) as interface for web page to android app
        5. Add WebViewClient for handling events that impact content rendering. You can also use this subclass to intercept URL loading.
        6. Add WebChromeClient for showing onConsoleMessage in logcat, enable fullscreen display.
        7. Add DownloadListener for downloading eg: pdfs etc
        */

        myWebViewHelper = new WebViewHelper((AppCompatActivity) getActivity(),binding.webViewMain);
        myWebViewHelper.setCalledFromFragment(this);
        // get the GOOGLE_ID_TOKEN from SecondFragment
        Bundle bundle = getArguments();
        if(bundle != null) {
            String loginType = bundle.getString("LOGIN_TYPE");
            myWebViewHelper.setLoginType(loginType);
            if(loginType != null) {
                switch (loginType) {
                    case "GOOGLE":
                        String googleIdTokenReceived = bundle.getString("GOOGLE_ID_TOKEN");
                        myWebViewHelper.setGoogleIdToken(googleIdTokenReceived);
                        break;
                    case "FACEBOOK":
                        String facebookAccessTokenReceived = bundle.getString("FACEBOOK_ACCESS_TOKEN");
                        myWebViewHelper.setFacebookAccessToken(facebookAccessTokenReceived);
                        break;
                }
            }
        }
        myWebViewHelper.setWebView();
        if(currentPertinantURL == null) {
            Log.i(TAG, "currentPertinantURL is " + currentPertinantURL + " so Loading home page");
            myWebViewHelper.loadHomePage();
        }
        else
        {
            Log.i(TAG, "currentPertinantURL is " + currentPertinantURL );
            myWebViewHelper.loadURL(currentPertinantURL);
        }
    }
    public void loadHomePage()
    {
        myWebViewHelper.loadHomePage();
    }
    public void refresh()
    {
        myWebViewHelper.refresh();
    }

    public void launchFacebookLoginActivity()
    {
        Bundle bundle = new Bundle();
        bundle.putString("PURPOSE_OF_CALL", "FACEBOOK_LOGIN_TO_WEBSITE");

        /*NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);*/
        go2SecondFragment(bundle);
    }

    public void launchFacebookLogoutActivity()
    {
        Bundle bundle = new Bundle();
        bundle.putString("PURPOSE_OF_CALL", "FACEBOOK_LOGOUT_FROM_APP");

        /*NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);*/
        go2SecondFragment(bundle);
        // to retrieve in next fragment getArguments().getString("PURPOSE_OF_CALL")
    }

    public void launchGoogleLoginActivity()
    {
        //https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application
        // Start the SecondActivity
        /*Intent intent = new Intent(this, GoogleSigninActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, "GOOGLE_LOGIN_TO_WEBSITE");
        startActivityForResult(intent, GOOGLE_SIGN_IN_ACTIVITY_REQUEST_CODE);*/



        //https://developer.android.com/guide/navigation/navigation-pass-data
        //https://stackoverflow.com/a/69630283
        Bundle bundle = new Bundle();
        bundle.putString("PURPOSE_OF_CALL", "GOOGLE_LOGIN_TO_WEBSITE");

        /*NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);*/
        go2SecondFragment(bundle);
        // to retrieve in next fragment getArguments().getString("PURPOSE_OF_CALL")

        /*try {
        }
        catch(IllegalStateException e)
        {
            //exceptionsjava.lang.IllegalStateException: Method setCurrentState must be called on the main thread
            Log.w("IllegalStateException Caught","exceptions"+e.getMessage());
            //e.printStackTrace();
        }*/
        // to avoid java.lang.IllegalStateException: Method setCurrentState must be called on the main thread
        //https://issuetracker.google.com/issues/171125856
        //binding.buttonFirst.callOnClick();
    }
    public void launchGoogleLogoutActivity()
    {
        Bundle bundle = new Bundle();
        bundle.putString("PURPOSE_OF_CALL", "GOOGLE_LOGOUT_FROM_APP");

        /*NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);*/
        go2SecondFragment(bundle);
        // to retrieve in next fragment getArguments().getString("PURPOSE_OF_CALL")

    }

    public  void replyLogout2WebViewPage()
    {
        //redundant, js function redirectAfterAndroidLogout() is called in onPageFinished of WebViewClient
    }
    public void launchGoogleReLoginActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("PURPOSE_OF_CALL", "RE_GOOGLE_LOGIN_TO_WEBSITE");
        Log.i(TAG,"launchGoogleReLoginActivity() called" );
        go2SecondFragment(bundle);
    }
    public void launchFacebookReLoginActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("PURPOSE_OF_CALL", "RE_FACEBOOK_LOGIN_TO_WEBSITE");
        Log.i(TAG,"launchFacebookReLoginActivity() called" );
        go2SecondFragment(bundle);
    }
    private void go2SecondFragment(Bundle bundle) {
        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);
    }
    // CUSTOM METHODS ENDS HERE
}
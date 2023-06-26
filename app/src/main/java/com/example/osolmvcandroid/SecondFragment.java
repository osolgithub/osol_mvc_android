package com.example.osolmvcandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.osolmvcandroid.databinding.FragmentSecondBinding;
import com.example.osolmvcandroid.mvc.helper.FacebookLogin;
import com.example.osolmvcandroid.mvc.helper.GoogleLogin;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

public class SecondFragment extends Fragment  implements View.OnClickListener {
    //GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 101;
    private static final String TAG  = "SecondFragment";
    private  String purposeOfActivity  = "";
    TextView txtGoogleLoggedInUser;
    Button btnCloseGoogleLoginActivity;

    private FragmentSecondBinding binding;

    private Boolean returnAfterSignOut = false;

    private FacebookLogin fbLoginHelper = null;
    private GoogleLogin googleLoginHelper = null;

    private AppCompatActivity currentActivity = null;

    boolean googlePurpose ;
    boolean facebookPurpose ;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                go2FirstFragment(null);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    //NON DEFAULT METHODS
    @Override
    public void onStart() {

        super.onStart();
        Log.d(TAG, "SecondFragment onStart ");
        setBtnCloseActivity();
        setPurposeofActivity();
        /*Handle Google Login*/
        if(googlePurpose && googleLoginHelper == null)
        {
            //setUpBaseForGoogleLogin();
            Log.d(TAG, "initiating googleLoginHelper " +
                    getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
            googleLoginHelper = GoogleLogin.getInstance();
            googleLoginHelper.initialize(this);

        }
        /*Handle Google Login ends here*/
        /*Handle FB Login*/
        //if(purposeOfActivity.equals("FACEBOOK_LOGIN_TO_WEBSITE"))
        if(facebookPurpose && fbLoginHelper == null)
        {
            Log.d(TAG, "initiating fbLoginHelper " +
                    getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
            //currentActivity = (AppCompatActivity) getActivity();
            fbLoginHelper = FacebookLogin.getInstance();
            fbLoginHelper.initialize(this);

            //Object fbProfile = fbLoginHelper.getFacebookProfile();
            //Log.d(TAG, "fbProfile is " + String.valueOf(fbProfile));
            /*if((purposeOfActivity.equals("RE_FACEBOOK_LOGIN_TO_WEBSITE") ||
                    purposeOfActivity.equals("FACEBOOK_LOGOUT_FROM_APP")))*/// && fbProfile != null)
            //block to signout was moved to facebookLoginHelper.initialize

        }
        /*Handle FB Login ends here*/


    }
    public void onClick(View v) {
        switch (v.getId()) {
            //btnGoogleSignIn is handled in googleLoginHelper
            /*case R.id.btnGoogleSignIn:
                googleLoginHelper.googleSignIn();
                break;
            //btnFacebookSignin is handled in fbLoginHelper
            case R.id.btnFacebookSignin:
                facebookSignIn();
                break;*/

            case R.id.btnGoogleSignOut: //https://developers.google.com/identity/sign-in/android/disconnect

                if(googlePurpose)
                {
                    googleLoginHelper.googleSignOut();
                }
                Log.d(TAG,"facebookPurpose is " + facebookPurpose + " " + getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
                if(facebookPurpose)
                {
                    returnAfterSignOut = true;
                    fbLoginHelper.signOut();
                }

                break;
            // ...
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        hideUnwantedControls();
        if(googlePurpose)googleLoginHelper.displayAccountOnResume();
        if(returnAfterSignOut){ //https://stackoverflow.com/a/13702104 How to execute a method just after the second onResume?
            Bundle bundle = new Bundle();
            String returnPurpose = "RETURN_AFTER_GOOGLE_LOGOUT";
            bundle.putString("PURPOSE_OF_CALL", returnPurpose);
            go2FirstFragment(bundle);
        }
        returnAfterSignOut = false;


    }
    //NON DEFAULT METHODS ENDS HERE

    //CUSTOM METHODS
    public void hideUnwantedControls()
    {
        //Log.d(TAG, "hideUnwantedControls() called "+ getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
        //Log.d(TAG, "purposeOfActivity is " + purposeOfActivity + " " + getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());

        binding.btnFacebookSignin.setVisibility(View.GONE);
        binding.btnGoogleSignIn.setVisibility(View.GONE);
        //binding.btnGoogleSignOut.setVisibility(View.GONE);

        binding.btnGoogleSignOut.setOnClickListener((View.OnClickListener) this);
        boolean googlePurpose = isGooglePurpose();
        boolean facebookPurpose = isFacebookPurpose();
        if(googlePurpose)
        {
            binding.btnGoogleSignIn.setVisibility(View.VISIBLE);
            binding.btnGoogleSignOut.setVisibility(View.VISIBLE);
        }
        if(facebookPurpose)
        {
            Log.d(TAG, "unhiding btnFacebookSignin "+ getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
            binding.btnFacebookSignin.setVisibility(View.VISIBLE);
        }
    }
    public boolean isGooglePurpose()
    {
        // check if the specified element
        // is present in the array or not
        // using Binary Search method
        String[] googlePurposes = {"GOOGLE_LOGIN_TO_WEBSITE",
                "RE_GOOGLE_LOGIN_TO_WEBSITE",
                "GOOGLE_LOGOUT_FROM_APP"};
        Arrays.sort(googlePurposes);//https://stackoverflow.com/a/13734743
        int res = Arrays.binarySearch(googlePurposes, purposeOfActivity);
        boolean googlePurpose = (res >= 0) ? true : false;
        return googlePurpose;
    }
    public boolean isFacebookPurpose()
    {
        String[] facebookPurposes = {"FACEBOOK_LOGIN_TO_WEBSITE",
                "RE_FACEBOOK_LOGIN_TO_WEBSITE",
                "FACEBOOK_LOGOUT_FROM_APP"};
        Arrays.sort(facebookPurposes);//https://stackoverflow.com/a/13734743
        int res = Arrays.binarySearch(facebookPurposes, purposeOfActivity);
        boolean facebookPurpose = (res >= 0) ? true : false;
        Log.d(TAG, "purposeOfActivity is " +  purposeOfActivity + " " + getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
        Log.d(TAG, "res is " +  res + " " + getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
        Log.d(TAG, "facebookPurpose is " +  facebookPurpose + " " + getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
        return facebookPurpose;
    }

    private void setBtnCloseActivity()
    {
        btnCloseGoogleLoginActivity = binding.btnCloseGoogleLoginActivity;
        btnCloseGoogleLoginActivity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /*NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);*/
                go2FirstFragment(null);
            }
        });
    }


    public String  getPurposeofActivity() {
        return purposeOfActivity;
    }
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

        googlePurpose = isGooglePurpose();
        facebookPurpose = isFacebookPurpose();
    }

    public void setReturnAfterSignOut(boolean returnAfterSignOut)
    {
        this.returnAfterSignOut = returnAfterSignOut;
    }
    private void googleSignIn()
    {
        // nothing to be done here since button click is registered in googleLoginHelper.setUpBaseForGoogleLogin() method , which is called in SecondFragment.onResume()
    }
    private void facebookSignIn()
    {
        // nothing to be done here since button click is registered in fbLoginHelper.registerCallBack() method , which is called in SecondFragment.onResume()
    }
    /*
    * onActivityResult is required to handle googleSignIn
    * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            googleLoginHelper.handleSignInResult(task);
        }
    }

    public void updateUIEmail(String email2Display)
    {
        //txtGoogleLoggedInUser = binding.txtGoogleLoggedInUser;
        txtGoogleLoggedInUser = getActivity().findViewById(R.id.txtGoogleLoggedInUser);
        if(txtGoogleLoggedInUser!=null)txtGoogleLoggedInUser.setText(email2Display);
    }

    public void go2FirstFragment(Bundle bundle) {
        NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment,bundle);
    }
    //CUSTOM METHODS ENDS HERE
}
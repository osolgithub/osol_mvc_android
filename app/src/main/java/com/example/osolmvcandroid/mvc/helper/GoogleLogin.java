package com.example.osolmvcandroid.mvc.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.osolmvcandroid.R;
import com.example.osolmvcandroid.SecondFragment;
import com.example.osolmvcandroid.mvc.MySuperAppApplication;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GoogleLogin  {
    private static GoogleLogin inst;

    public Context currentContext;


    private AppCompatActivity currentActivity;

    private SecondFragment currentFragment;

    private final String TAG = "GoogleLoginHelper";

    private String purposeOfActivity;


    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount lastSignedInAccount;

    public static GoogleLogin getInstance()
    {

        if(inst == null)
        {
            inst = new GoogleLogin();
        }
        return inst;
    }
    private GoogleLogin() {
        currentContext = MySuperAppApplication.getContext();
    }
    public void initialize(SecondFragment currentFragment) {
        setCurrentFragment(currentFragment);
        setCurrentActivity((AppCompatActivity) currentFragment.getActivity());

        //String purposeOfCalledFragment = ((SecondFragment)currentFragment).getPurposeofActivity()
        String purposeOfCalledFragment = currentFragment.getPurposeofActivity();
        setPurposeOfActivity(purposeOfCalledFragment);

        setUpBaseForGoogleLogin();
    }
    public void setPurposeOfActivity(String purposeOfActivity)
    {
        this.purposeOfActivity = purposeOfActivity;
    }
    private void setUpBaseForGoogleLogin(){
        Log.d(TAG, "setUpBaseForGoogleLogin called " +
                getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(currentFragment.getString(R.string.google_login_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(currentActivity, gso);





        // Set the dimensions of the sign-in button.
        //(LoginButton) currentActivity.findViewById(R.id.btnFacebookSignin);
        SignInButton signInButton = currentActivity.findViewById(R.id.btnGoogleSignIn);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //signInButton.setOnClickListener((View.OnClickListener) this);
        /*signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });*/
        signInButton.setOnClickListener(view -> GoogleLogin.getInstance().googleSignIn());




        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(currentFragment.getContext());
        Log.d (TAG, "lastSignedInAccount is " + lastSignedInAccount +
                getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
        /*if((purposeOfActivity.equals("RE_GOOGLE_LOGIN_TO_WEBSITE") ||
                purposeOfActivity.equals("GOOGLE_LOGOUT_FROM_APP")) && account != null)*/
        String[] googleLogoutPurposes = {
                "RE_GOOGLE_LOGIN_TO_WEBSITE",
                "GOOGLE_LOGOUT_FROM_APP"
        };
        Log.d(TAG, "purposeOfActivity is " + purposeOfActivity +
                getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
        if(GenericHelper.in_array(googleLogoutPurposes,purposeOfActivity) && lastSignedInAccount != null)
        {
            currentFragment.setReturnAfterSignOut(true);
            googleSignOut();
        }
        else {
            //updateUI(account);
        }
    }
    public void displayAccountOnResume()
    {
        if(lastSignedInAccount != null)
        {
            updateUI(lastSignedInAccount);
        }
        else
        {
            currentFragment.updateUIEmail("Not Logged In");
        }
    }
    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());//https://developers.google.com/android/reference/com/google/android/gms/common/api/CommonStatusCodes
            updateUI(null);
        }
    }

    public void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        currentFragment.startActivityForResult(signInIntent, currentFragment.RC_SIGN_IN);// result will be caught in SecondFragment.onActivityResult
    }
    public void googleSignOut() {
        Log.w(TAG, "signOut: Signing out");
        /*getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });*/
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(currentFragment.getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Log.w(TAG, "onComplete: logged out");
                        updateUI(null);
                        /*if(purposeOfActivity.equals("GOOGLE_LOGOUT_FROM_APP"))
                        {
                            currentFragment.setReturnAfterSignOut(true);
                        }*/
                    }
                });
    }
    private void updateUI(GoogleSignInAccount account)
    {
        if(account == null)
        {
            Log.w(TAG, "updateUI: notLogged in" );
            //txtGoogleLoggedInUser.setText("notLogged in");
            currentFragment.updateUIEmail("notLogged in");
            //
        }
        else
        {
            String googleIdToken = account.getIdToken();
            String accountEmail = account.getEmail();
            //txtGoogleLoggedInUser.setText(accountEmail);
            currentFragment.updateUIEmail(accountEmail);
            Log.w(TAG, "Logged in!! updateUI: "+account.getEmail() +
                    " id token is " + googleIdToken
            );
            Boolean purposeCheck = purposeOfActivity.equals("GOOGLE_LOGIN_TO_WEBSITE") || purposeOfActivity.equals("RE_GOOGLE_LOGIN_TO_WEBSITE");
            Log.w(TAG, "updateUI: Logged purposeOfActivity " + purposeOfActivity );
            Log.w(TAG, "updateUI: Logged purposeCheck " + purposeCheck +
                        getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());

            if(purposeCheck)
            {
                Log.w(TAG, "updateUI: returning after Logged in" );

                /*Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_TEXT, googleIdToken);
                setResult(RESULT_OK, intent);
                finish();// close this activity*/
                //https://stackoverflow.com/a/69630283
                Bundle bundle = new Bundle();
                bundle.putString("LOGIN_TYPE", "GOOGLE");
                bundle.putString("GOOGLE_ID_TOKEN", googleIdToken);
                bundle.putString("PURPOSE_OF_CALL", "RETURN_AFTER_GOOGLE_LOGIN");

                /*NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment,bundle);*/
                currentFragment.go2FirstFragment(bundle);
                // to retrieve in next fragment getArguments().getString("GOOGLE_ID_TOKEN")
            }//if(purposeOfActivity == "GOOGLE_LOGIN_TO_WEBSITE")

            //https://stackoverflow.com/questions/52415960/how-can-i-get-this-google-login-id-token-from-this-android-app-to-verify-server
            //Logged in!! updateUI: outsourceol@gmail.com id token is eyJhbGciOiJSUzI1NiIsImtpZCI6ImZkYjQwZTJmOTM1M2M1OGFkZDY0OGI2MzYzNGU1YmJmNjNlNGY1MDIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMDEzMzA2Mzc4NDMzLWx2aWJybXRtYmwzc2VraTVrcWE2NGoyb3FybGdubTBkLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiMTAxMzMwNjM3ODQzMy0yZW12ZmRvNWN2cTVrY2dxYXEyc2ZoZXQ4YzViMmpxcC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExMDY1NzQ4NDM3NTE3MzYxNjc0MyIsImVtYWlsIjoib3V0c291cmNlb2xAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJPdXRzb3VyY2UgT25saW5lIiwicGljdHVyZSI6Imh0dHBzOi8vbGg2Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tNjdCNTktTW1zSTQvQUFBQUFBQUFBQUkvQUFBQUFBQUFBQUEvQU1adXVjbWJITjVOTzQ5dnJ5WERpUnc1Ql9NWElhNlZSUS9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiT3V0c291cmNlIiwiZmFtaWx5X25hbWUiOiJPbmxpbmUiLCJsb2NhbGUiOiJlbiIsImlhdCI6MTYxMzQ5NTQzNCwiZXhwIjoxNjEzNDk5MDM0fQ.PvCK2CesUIjezEdqYEKJf8vyaPaT_bfYK-qPfa-ZLrzLo1hLMIc47h_2wu2hwLXuMQB4VUqs_qLsAEFw9XC-N-aYtsutZjyyrJg0VS06O66zOk_RlsgiB_whZAG7Xol98OW0VPLdSyh55N-qGl_gSQAWann5mUflywPaqWIKNak-Cx222yRHkS_ymRcuvwZd6ZF-QXoGKvg3LvA6kM37LrujuI4Ilw9WuIKciQEpqudqM2F_Li64lZFT21Tcyb-94kqKExpieV4HDvfVGPZYV5LFddMzCYjoYljxpFjdMy8mjZHFY04QTJref1GOacx0aYkns9cyV2hStLjt8_D1tg

            //send id_token back to browse activity
            //using startActivityForResult and onActivityResult : https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application
            //post id_token via ajax to site

            //in website
            // set up web page to ajax_submit id_token
            //submit id_token with ajax
            //verify id token
            //update user_record with info from id_token
            // set sessions
            //redirect to viewSavedResources

            // put the String to pass back into an Intent and close this activity

        }

    }
    private void setCurrentFragment(SecondFragment currentFragment) {
        this. currentFragment =  currentFragment;
    }

    public void setCurrentActivity(AppCompatActivity currentActivity)
    {
        this.currentActivity = currentActivity;
    }


}

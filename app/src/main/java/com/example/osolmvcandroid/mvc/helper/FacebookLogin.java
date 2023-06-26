package com.example.osolmvcandroid.mvc.helper;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoginStatusCallback;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.example.osolmvcandroid.R;
import com.example.osolmvcandroid.SecondFragment;
import com.example.osolmvcandroid.mvc.MySuperAppApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * app url https://developers.facebook.com/apps/191136896803848/settings/basic/
 * based on https://developers.facebook.com/docs/facebook-login/android/
 * verification from server side https://stackoverflow.com/questions/24068883/verify-facebook-api-access-token-userid-via-php-sdk
 * FB logout Android code https://stackoverflow.com/a/53987347
 * development key command make sure %PATH% includes Android Java home (C:/Program Files/Android/Android Studio/jbr/bin)
 *
 * Key hashes are 28 characters including the trailing = and are limited to the following characters: [a-zA-Z0-9+/=]
 *
 * keytool -exportcert -alias androiddebugkey -keystore "C:\Users\USERNAME\.android\debug.keystore" | "PATH_TO_OPENSSL_LIBRARY\bin\openssl" sha1 -binary | "PATH_TO_OPENSSL_LIBRARY\bin\openssl" base64
 * keytool -exportcert -alias androiddebugkey -keystore C:\Users\sreek\.android\debug.keystore | D:\OpenSSLWin\bin\openssl sha1 -binary | D:\OpenSSLWin\bin\openssl base64
 * Enter keystore password: 123456
 * VzQ/QT8/Py0/Pzw/Az8/LEtbGVsK
 * Package name: com.example.osolmvcandroid
 * ClassName: MainActivity
 *
 * Errors Encountered :
 * https://stackoverflow.com/questions/20921897/android-hash-key-cant-connect-with-facebook
 * https://slproweb.com/products/Win32OpenSSL.html
 *
 * keytool -exportcert -alias androiddebugkey -keystore C:\Users\sreek\.android\debug.keystore | C:\SlProwebOpenSSLWin64\bin\openssl sha1 -binary | C:\SlProwebOpenSSLWin64\bin\openssl base64
 * if `slproweb OpenSSL` also doesnt work, just `add/copy the hashkey shown in the error message` in app settings for android platform
 *
 *
 *
 * https://stackoverflow.com/questions/29642759/profile-getcurrentprofile-returns-null-after-logging-in-fb-api-v4-0
 *
 * If this is happening during development, it is likely that
 * `you logged in to a different account in facebook app`, that is **not allotted any role like admin,tester etc**
 *
 * https://stackoverflow.com/questions/73193526/app-not-active-this-app-is-not-currently-accessible-and-the-app-developer-is-aw
 *
 * This bug is similar to above one
 * If this is happening during development, it is likely that
 * `you logged in to a different account in facebook app`, that is **not allotted any role like admin,tester etc**
 *
 * If this is happening in the released app, trye [this solution](https://stackoverflow.com/a/73328805)
 * [Facebook Privacy Policy](https://docs.google.com/document/d/1L3Z6P6a5AIC0cWBOBIHd9XOR1gqQruOo6hmM99TtVYM/edit)
 *

 *
 *
 * https://stackoverflow.com/questions/76345578/one-or-more-invalid-package-names-make-sure-package-names-are-associated-with
 * Solution: Make a copy of tha android project & use an existing playstore package name for development. i used com.urbanclap.urbanclap
 * To change packagename, copy the project folder in windows explorer
 * change package name with refactor
 * references for copying an android project to different name
 * https://sebhastian.com/android-studio-copy-project/
 * https://stackoverflow.com/a/29092698
 *
 *
 * android error cannot resolve symbol 'databinding' after copying project https://stackoverflow.com/questions/32815608/android-data-binding-cannot-resolve-symbol
 *
 * Replace all in project (Ctrl+shift + R) 'com.urbanclap.urbanclap' to com.example.osolmvcandroid
 * Gradle sync
 * if it still doesnt work  select Build >> Rebuild Project
 *
 *
 * inititiation
 * com.example.osolmvcandroid.mvc.helper.FacebookLogin.getInstance()
 */
public class FacebookLogin {
    private static FacebookLogin inst;

    public Context currentContext;
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    private static final String EMAIL = "email";

    private AppCompatActivity currentActivity;

    private Fragment currentFragment;

    private String logTag = "FacebookLoginHelper";

    private ProfileTracker profileTracker;

    private boolean signoutOnProfileSet = false;

    private String TAG = "FacebookLogin";

    public static FacebookLogin getInstance()
    {

        if(inst == null)
        {
            inst = new FacebookLogin();
        }
        return inst;
    }
    private FacebookLogin() {
        currentContext = MySuperAppApplication.getContext();
    }
    public void initialize(Fragment currentFragment){
        Log.d(TAG, "calling FacebookLogin.initialize " +
                getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
        if(callbackManager == null)
        {
            Log.d(TAG, "callbackManager is null " +
                    getClass().getName() + " " + Thread.currentThread().getStackTrace()[2].getLineNumber());
            callbackManager = CallbackManager.Factory.create();
        }
            setCurrentFragment(currentFragment);
            setCurrentActivity((AppCompatActivity) currentFragment.getActivity());
            registerCallBack();

        /*block to be tested*/
        /*this block is moved from second fragment*/
        String purposeOfActivity =  ((SecondFragment)currentFragment).getPurposeofActivity();
        String[] fbLogoutPurposes = {
                "RE_FACEBOOK_LOGIN_TO_WEBSITE",
                "FACEBOOK_LOGOUT_FROM_APP"};
        if(GenericHelper.in_array(fbLogoutPurposes,purposeOfActivity))
        {
            Log.d(TAG, "tag2SignOut fb User "+ getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
            //fbLoginHelper.tag2SignOut();
            ((SecondFragment)currentFragment).setReturnAfterSignOut(true);
            signOut();
        }
        /*block to be tested ends here*/

    }
    private void updateProfileView(Profile profile) {
        if (profile != null) {
            String status = String.format("Status: logged!\nUser: %s", profile.getName());
            Log.d("DBG", profile.getProfilePictureUri(200,200).toString());
            //tvStatus.setText(status);
            if(signoutOnProfileSet)
            {
                signOut();
            }
        } else {
            //tvStatus.setText("Status: ready");
        }
    }
    private void setCurrentFragment(Fragment currentFragment) {
        this. currentFragment =  currentFragment;
    }

    public void setCurrentActivity(AppCompatActivity currentActivity)
    {
        this.currentActivity = currentActivity;
    }
    public void registerCallBack()
    {




        loginButton = (LoginButton) currentActivity.findViewById(R.id.btnFacebookSignin);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this); otherwise remove the line
        loginButton.setFragment(currentFragment);
        Log.d(TAG, "calling loginButton.registerCallback " +
                getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                /* get name and update textbox
                * get access token
                * return to first fragment
                * confirm access token
                *       verification from server side https://stackoverflow.com/questions/24068883/verify-facebook-api-access-token-userid-via-php-sdk
                * on verification success go back to second fragment

                 */
                Log.d(TAG, "loginButton.registerCallback.onSuccess " +
                        getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
                AccessToken  accessToken = loginResult.getAccessToken();
                extractUserInfo( accessToken);

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    private void returnAccessToken2CallerActivity(AccessToken accessToken)
    {
        //AccessToken  accessToken = loginResult.getAccessToken();
        //AccessToken token = AccessToken.getCurrentAccessToken();
        //Log.d(logTag,"access token is: "+String.valueOf(accessToken));
        /*
        https://stackoverflow.com/a/30078648
        to display accessToken in Logcat, you need to use accessToken.getToken()
        if we use String.valueOf(accessToken), it will show only ACCESS_TOKEN_REMOVED
        {AccessToken token:ACCESS_TOKEN_REMOVED permissions:[openid, public_profile, email]}
         */
        String accessTokenValOnly = accessToken.getToken();
        Log.d(logTag,"access token is: "+ accessTokenValOnly + " " +  getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber());
        //
        Bundle bundle = new Bundle();
        bundle.putString("LOGIN_TYPE", "FACEBOOK");
        bundle.putString("FACEBOOK_ACCESS_TOKEN", accessTokenValOnly);
        bundle.putString("PURPOSE_OF_CALL", "RETURN_AFTER_FACEBOOK_LOGIN");

        ((SecondFragment)currentFragment).go2FirstFragment(bundle);
    }
    public void prepare4ExpressLogin()
    {

        LoginManager.getInstance().retrieveLoginStatus(currentContext, new LoginStatusCallback() {
            @Override
            public void onCompleted(AccessToken accessToken) {
                // User was previously logged in, can log them in directly here.
                // If this callback is called, a popup notification appears that says
                // "Logged in as <User Name>"
                //Profile fbProfile = Profile.getCurrentProfile();
                Log.d(TAG,"Expresslogin onCompleted. " +
                        getClass().getName() + " " +Thread.currentThread().getStackTrace()[2].getLineNumber()) ;
                Profile fbProfile = getFacebookProfile();
                String fbName = fbProfile.getName();
                ((SecondFragment)currentFragment).updateUIEmail(fbName);
                returnAccessToken2CallerActivity(accessToken);
            }
            @Override
            public void onFailure() {
                // No access token could be retrieved for the user
            }
            @Override
            public void onError(Exception exception) {
                // An error occurred
            }
        });
    }
    protected void extractUserInfo(AccessToken accessToken)
    {
        //https://stackoverflow.com/questions/41278182/how-to-handle-the-logout-button-click-in-fb-sdk-4-18-0
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                // this callback is triggered on logout
                if (currentAccessToken == null) {
                    //write your code here what to do when user logout
                    Log.d(TAG, "FB Logged out");
                    //txtFBUser.setText("Not Logged In");
                }
            }
        };

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken/*loginResult.getAccessToken()*/,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("FB_LOGIN_TEST", "Roken Request Response is " + response.toString());

                        try {
                            String Name = object.getString("name");

                            String FEmail = object.getString("email");
                            Log.v("Email = ", " " + FEmail);
                            Toast.makeText(currentContext, "Name " + Name, Toast.LENGTH_LONG).show();

                            //txtFBUser.setText(Name + " " + FEmail);
                            ((SecondFragment)currentFragment).updateUIEmail(FEmail);
                            returnAccessToken2CallerActivity(accessToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        //parameters.putString("fields", "id,name,email,gender,birthday");
        parameters.putString("fields", "id,name,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();

        //https://stackoverflow.com/questions/14996868/android-facebook-get-user-access-token-on-successful-login
        /*GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                accessToken ,//login_result.getAccessToken()
                //AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,//bundle
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray rawName = response.getJSONObject().getJSONArray("data");
                            Log.d(logTag,"rawName friendList"+String.valueOf(rawName));
                            AccessToken token = AccessToken.getCurrentAccessToken();
                            Log.d(logTag,"access token is: "+String.valueOf(token));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();*/
    }
    public void signOut()
    {
        //Profile fbProfile = Profile.getCurrentProfile();//https://stackoverflow.com/a/29436837
        Profile fbProfile = getFacebookProfile();
        if (fbProfile == null) {
            Log.d(logTag, "NOT logged in");
        }
        else
        {
            String fbName = fbProfile.getName();
            Log.d(logTag, "fbProfile.getName() is " + fbName + ", Logging Out");

            //https://stackoverflow.com/a/53987347
            LoginManager.getInstance().logOut();
            //also check https://stackoverflow.com/q/35957547
        }
    }
    public Profile getFacebookProfile()
    {
        Profile facebookProfile = Profile.getCurrentProfile();//https://stackoverflow.com/a/29436837
        return facebookProfile;
    }

    public void tag2SignOut() {
        signoutOnProfileSet = true;
    }

}

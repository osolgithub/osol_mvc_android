## Sequence of control

### Google login

1. User clicks login
2. `OSOLMVCAndroidJSInterface.osolmvcGoogleLogin()` method called
3. `FirstFragment.launchGoogleLoginActivity` is called in AppInterface4CallsFromJS
3. User sent to SecondFragment via NavController
```
Bundle bundle = new Bundle();
        bundle.putString("PURPOSE_OF_CALL", "GOOGLE_LOGIN_TO_WEBSITE");

        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);
        // to retrieve in next fragment getArguments().getString("PURPOSE_OF_CALL")
```	
4. in `onStart` of SecondFragment
```
setUpBaseForGoogleLogin();
```
which inturn  sets
```
.
.
		btnCloseGoogleLoginActivity.setOnClickListener
.
.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
.
.
		SignInButton signInButton = binding.btnGoogleSignIn;
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener((View.OnClickListener) this);
        binding.btnGoogleSignOut.setOnClickListener((View.OnClickListener) this);
```	
4. google login is called in `SecondFragment.onClick` which calls `SecondFragment.signin`
```
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
```
5. in `onActivityResult` 
```
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
```
6. in `handleSignInResult`
```
			GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
			
```
7. in `updateUI`
```
			String googleIdToken = account.getIdToken();
            String accountEmail = account.getEmail();
			.
			.
			.
				Bundle bundle = new Bundle();
                bundle.putString("GOOGLE_ID_TOKEN", googleIdToken);
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment,bundle);
                // to retrieve in next fragment getArguments().getString("GOOGLE_ID_TOKEN")
			
```
8. `onStart()` of FirstFragment calls `myWebViewHelper.setWebView()`
9. in `FirstFragment.setWebView()`
```
        Bundle bundle = getArguments();
		if(bundle != null) {
            String googleIdTokenReceived = bundle.getString("GOOGLE_ID_TOKEN");
            myWebViewHelper.setGoogleIdToken(googleIdTokenReceived);
        }
        myWebViewHelper.setWebView();
		.
		.
		.
		myWebViewHelper.loadURL(currentPertinantURL);//currentPertinantURL is login page
```
10. In `WebViewClient.onPageFinished`
```
			.
			.
			.
		currentWebViewHelper.verifyGoogleIDToken();
		currentWebViewHelper.setGoogleIdToken(null);
```
11. currentWebViewHelper.verifyGoogleIDToken();
```
if(googleIdToken != null)
{
	call browser function verifyGoogleIdToken('"+googleIdToken+"');
}
```
12. `verifyGoogleIdToken` function verifies googleIdToken with AJAX, if result id failed, it calls `OSOLMVCAndroidJSInterface.osolmvcGoogleLoginFailed();` which again calls `FirstFragment.launchGoogleLoginActivity`


### Activity flow
```
@startuml
start

:step 1) User clicks login in **Account/Login Page**
**OSOLMVCAndroidJSInterface.osolmvcGoogleLogin()** method called;

:step 2)**AppInterface4CallsFromJS.osolmvcGoogleLogin()** calls **FirstFragment.launchGoogleLoginActivity**
User sent to SecondFragment via NavController;

:step 3) in **SecondFragment.onStart**
**SecondFragment.setUpBaseForGoogleLogin()** is called;

:step 4) **SecondFragment.setUpBaseForGoogleLogin()** contains
btnCloseGoogleLoginActivity.setOnClickListener
GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
SignInButton signInButton = binding.btnGoogleSignIn
signInButton.setSize(SignInButton.SIZE_STANDARD)

**signInButton.setOnClickListener((View.OnClickListener) this)**
binding.btnGoogleSignOut.setOnClickListener((View.OnClickListener) this);

:step 5) **SecondFragment.onClick**
calls **SecondFragment.signin**;

:step 6) SecondFragment.signin contains
Intent signInIntent = mGoogleSignInClient.getSignInIntent()
**startActivityForResult(signInIntent, RC_SIGN_IN)**;

:step 7) **onActivityResult** contains
Task<GoogleSignInAccount> task = **GoogleSignIn.getSignedInAccountFromIntent(data)**
**handleSignInResult(task)**;

:step 8) **SecondFragment.handleSignInResult** contains
**googleSignInAccount account = completedTask.getResult(ApiException.class)**
// Signed in successfully, show authenticated UI.
updateUI(account);

:step 9) **SecondFragment.updateUI** contains
String **googleIdToken = account.getIdToken()**
bundle.putString("GOOGLE_ID_TOKEN", googleIdToken)
NavHostFragment.findNavController(SecondFragment.this)		
.**navigate**(R.id.action_SecondFragment_to_FirstFragment,bundle);

:step 10) **FirstFragment.onStart()** calls
setWebView();

:step 11) **setWebView** contains 
Bundle bundle = getArguments()
if(bundle != null) 
	String googleIdTokenReceived = bundle.getString("GOOGLE_ID_TOKEN")
	myWebViewHelper.setGoogleIdToken(googleIdTokenReceived)
endif
**myWebViewHelper.setWebView()**
.
.
.
**myWebViewHelper.loadURL**(currentPertinantURL)//currentPertinantURL is login page;

:step 12) **WebViewClient.onPageFinished** contains
**currentWebViewHelper.verifyGoogleIDToken()**
currentWebViewHelper.setGoogleIdToken(null);

:step 13) **currentWebViewHelper.verifyGoogleIDToken()** contains
if(googleIdToken != null)
	**call browser js function verifyGoogleIdToken('"+googleIdToken+"')**
endif;

:step 14) verifyGoogleIdToken js function verifies googleIdToken with AJAX, 
if result is failed, it calls **OSOLMVCAndroidJSInterface.osolmvcGoogleLoginFailed()**
 which again calls **FirstFragment.launchGoogleLoginActivity**;



stop
@enduml
```

### Diagram with [PUML SERVER](http://www.plantuml.com/plantuml/uml/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000)

![Alt text](file://googleLogin.png "Text on mouseover")
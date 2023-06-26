### Google logout

1. User clicks logout
2. OSOLMVCAndroidJSInterface.osolmvcGoogleLogout() method called
3. User sent to Second frame via NavController
4. google logout is called in onStart which calls `GoogleSignInClient.signOut`
5. `onComplete` method of GoogleSignInClient.signOut sends control to first frame
6. First frame calls JS `redirectAfterAndroidLogout()`


## Page rendering flow chart

### Nav Graph

1. This app uses NavGraph `@navigation/nav_graph` with `startDestination="@id/FirstFragment"`
2. this nav graph is situated in `@layout/content_main.xml`, which in turn is included in `@layout/activity_main.xml`
```
activity_main >> content_main >> nav_host_fragment_content_main
                       >> @navigation/nav_graph >> (FirstFragment & SecondFragment)
```
3. Nav Graph uses  2 fragments 
	1. @layout/fragment_first which has action `"@+id/action_FirstFragment_to_SecondFragment"` and destination `@id/SecondFragment`
	2. @layout/fragment_second which has action `"@+id/action_SecondFragment_to_FirstFragment"` and destination `@id/FirstFragment`
4. Activity of @layout/fragment_first is `FirstFragment.java` , 
5. FirstFragment contains WebView which Loads url set as `homePage` in `mvc.Config`
5. When the user reaches login page, he is prompted with login button. on clicking the login button, a javascript function calls android method, and the call is handled by `mvc.view.components.webview.AppInterface4CallsFromJS`
```
javascript:OSOLMVCAndroidJSInterface.osolmvcGoogleLogin()
```
6. AppInterface4CallsFromJS.osolmvcGoogleLogin() calls method in FirstFragment
```
currentActivity.runOnUiThread(() -> ((FirstFragment) calledFromFragment).launchGoogleLoginActivity());
```

7. graph action is initiated in `FirstFragment` as 

```
		///https://developer.android.com/guide/navigation/navigation-pass-data
        //https://stackoverflow.com/a/69630283
        Bundle bundle = new Bundle();
        bundle.putString("PURPOSE_OF_CALL", "GOOGLE_LOGIN_TO_WEBSITE");//PURPOSE_OF_CALL could also be RE_GOOGLE_LOGIN_TO_WEBSITE, for that different action is taken
        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);
        // to retrieve in next fragment getArguments().getString("PURPOSE_OF_CALL")
```
6. In the case of navGraph fragment `@id/SecondFragment`, graph action is called after google login  as follows
```
				Bundle bundle = new Bundle();
                bundle.putString("GOOGLE_ID_TOKEN", googleIdToken);

                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment,bundle);
                // to retrieve in next fragment getArguments().getString("GOOGLE_ID_TOKEN")
```	
## WebView 

WebView implementation requires 3 additional classes
1. WebView (Better use the methods in class `WebViewHelper`) : loads URLs
2. AppInterface4Javascript : handle calls from browser. browser call methods should be prefixed `@JavascriptInterface`
3. WebviewClient : handles `onPageFinished`, `shouldOverrideUrlLoading`, `onReceivedSslError` etc
4. WebChromeClient : `onConsoleMessage`, `onJsAlert`, `Full sceen display` etc

## Sequence of control

1. MainActivity.java is launched , because in `AndroidManifest.xml`, it is set as action `<action android:name="android.intent.action.MAIN" />` and `<category android:name="android.intent.category.LAUNCHER" />`
```
		<activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.OSOLMVCAndroid.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
```
2. The Layout associated with MainActivity is `activity_main.xml` by `tools:context=".MainActivity"`

```
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
```

3. In activity_main.xml there is `<include layout="@layout/content_main" />`
4. `content_main.xml` includes `@navigation/nav_graph` as `NavHostFragment`.
```
activity_main >> content_main >> nav_host_fragment_content_main
                       >> @navigation/nav_graph >>
```
Tag is 

```
	<fragment
        android:id="@+id/nav_host_fragment_content_main"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:defaultNavHost="true"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:navGraph="@navigation/nav_graph" />
```
5. `navigation/nav_graph.xml` declares `FirstFragment` (which is `@layout/fragment_first` and `destination="@id/SecondFragment"`) and `SecondFrament` (which is `@layout/fragment_second`)

First fragment is
```
	<fragment
        android:id="@+id/FirstFragment"
        android:name="com.example.osolmvcandroid.FirstFragment"
        android:label="@string/first_fragment_label"
        tools:layout="@layout/fragment_first">
        <action
            android:id="@+id/action_FirstFragment_to_SecondFragment"
            app:destination="@id/SecondFragment" />
    </fragment>
```

Second Fragment is

```
    <fragment
        android:id="@+id/SecondFragment"
        android:name="com.example.osolmvcandroid.SecondFragment"
        android:label="@string/second_fragment_label"
        tools:layout="@layout/fragment_second">

        <action
            android:id="@+id/action_SecondFragment_to_FirstFragment"
            app:destination="@id/FirstFragment" />
        <argument
            android:name="method2Execute"
            app:argType="string"
            android:defaultValue="@null"
            app:nullable="true" />
    </fragment>
```

3. In `onCreate(Bundle savedInstanceState)` of MainActivity, initializes `Navigation graph`
```
		/*
        * https://developer.android.com/topic/libraries/data-binding/generated-binding
        * A binding class is generated for each layout file.
        * By default, the name of the class is the name of the layout file converted to Pascal case
        * with the Binding suffix added to it. So, for example,
        * if the layout filename is `activity_main.xml`,
        * the corresponding generated class is `ActivityMainBinding`.
        * This class holds all the bindings from the layout properties
        * to the layout's views and knows how to assign values for the binding expressions.
        **/
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        /*
        * https://developer.android.com/guide/navigation
        * https://www.youtube.com/watch?v=Y0Cs2MQxyIs
        * (Jet Pack play list) https://www.youtube.com/playlist?list=PLWz5rJ2EKKc9mxIBd0DRw9gwXuQshgmn2
        *   1. Navigation graph
        *       Resource component, an XML file. in this case `res/navigation/nav_graph.xml` folder.
        *       Information can be visualized with navigation editor from Android 3.3 onwards
        *   2. NavHostFragment
        *       Fragment widget added to your layout. Basically a window that swaps different
        *       destination fragments included in navigation graph.
        *       in this case , <fragment android:id="@+id/nav_host_fragment_content_main"
        *       in `res/layout/content_main.xml`
        *   3. NavController
        *       Each NavHostFragment has a NavController.
        *       Executes instructions mentioned in Navigation graph
        *
        *       ie activity_main >> content_main >> nav_host_fragment_content_main
        *               >> nav_graph >>
        *           (startDestination="@id/FirstFragment" action_FirstFragment_to_SecondFragment)
        * https://developer.android.com/guide/navigation/navigation-getting-started
        * https://developer.android.com/guide/navigation/navigation-navigate
        * https://developer.android.com/reference/androidx/navigation/Navigation#findNavController(android.app.Activity,kotlin.Int)
         */
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        /*
        * link `back` button in toolbar to `graph` via `NavController` & `NavigationUI`
        * AppBarConfiguration https://developer.android.com/reference/androidx/navigation/ui/AppBarConfiguration
        * Configuration options for NavigationUI methods that interact with implementations of the
        * app bar pattern such as androidx.appcompat.widget.Toolbar,androidx.appcompat.app.ActionBar etc
        * https://stackoverflow.com/questions/44516512/what-is-exact-difference-between-appbar-toolbar-actionbar-and-when-to-use-th
         */
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        /*
        *   PLANNED WORK FLOW
            1. WebView in first fragment
                1. Server side page for android google login
                2. on click google login call graph `action action_FirstFragment_to_SecondFragment`
            2. google login handling in second fragment
                1. prompts google login
                2. gets id token
                3. calls graph `action action_SecondFragment_to_FirstFragment` , along with passing of id token to first fragment
                4. Webapp sends id token to server, verifies and send reply, if verified automatically redirected to `profile` page
                5. if not verified, show error message ing snackbar & call graph `action action_FirstFragment_to_SecondFragment`
        * */
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
```		
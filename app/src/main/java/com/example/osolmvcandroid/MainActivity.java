package com.example.osolmvcandroid;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.osolmvcandroid.databinding.ActivityMainBinding;
import com.example.osolmvcandroid.mvc.helper.GoogleLogin;
import com.google.android.material.snackbar.Snackbar;
/*
* bitbucket url
*/
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_home) {
            Fragment firstFragment = getCurrentVisibleFragment();//https://stackoverflow.com/a/62179052
            if(firstFragment instanceof FirstFragment)
            {
                ((FirstFragment)firstFragment).loadHomePage();
            }
            return true;
        }
        if (id == R.id.action_reload) {
            //https://stackoverflow.com/a/51732378
            /*FragmentManager supportFragmentManager = getSupportFragmentManager();
            NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
            Fragment firstFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);*/
            Fragment firstFragment = getCurrentVisibleFragment();//https://stackoverflow.com/a/62179052
            if(firstFragment instanceof FirstFragment)
            {
                ((FirstFragment)firstFragment).refresh();
            }
            return true;
        }
        if (id == R.id.action_google_login) {
            //https://stackoverflow.com/a/51732378
            /*FragmentManager supportFragmentManager = getSupportFragmentManager();
            NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
            Fragment firstFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);*/
            Fragment secondFragment = getCurrentVisibleFragment();//https://stackoverflow.com/a/62179052
            if(secondFragment instanceof SecondFragment)
            {

                //((SecondFragment)secondFragment).googleSignIn();
                GoogleLogin googleLoginHelper =  GoogleLogin.getInstance();
                googleLoginHelper.googleSignIn();
            }
            else
            {
                Toast.makeText(this, "Sorry, you cant trigger google login in this fragment.", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }




    //CUSTOM METHODS
    //https://stackoverflow.com/a/62179052
    private Fragment getCurrentVisibleFragment() {
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().getPrimaryNavigationFragment();
        FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
        Fragment fragment = fragmentManager.getPrimaryNavigationFragment();
        if(fragment instanceof Fragment ){
            return fragment ;
        }
        return null;
    }
}
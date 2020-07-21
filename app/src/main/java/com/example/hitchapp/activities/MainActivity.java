package com.example.hitchapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hitchapp.R;
import com.example.hitchapp.fragments.ComposeFragment;
import com.example.hitchapp.fragments.HomeFragment;
import com.example.hitchapp.fragments.MyRidesFragment;
import com.example.hitchapp.fragments.MyProfileFragment;
import com.example.hitchapp.fragments.RequestsFragment;
import com.example.hitchapp.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_my_rides:
                        fragment = new MyRidesFragment();
                        break;
                    case R.id.action_post:
                        User user = (User) ParseUser.getCurrentUser();
                        if(user.isDriver())
                            fragment = new ComposeFragment();
                        else{
                            Toast.makeText(MainActivity.this, "You need to setup a driver profile to post a ride", Toast.LENGTH_SHORT).show();
                            fragment = new MyProfileFragment();
                        }
                        break;
                    case R.id.action_profile:
                    default:
                        fragment = new MyProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(TAG).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.miRequests:
                fragment = new RequestsFragment();
                break;
            default:
                fragment = new HomeFragment();
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(TAG).commit();
        return true;
    }
}
package com.example.hitchapp.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.hitchapp.R
import com.example.hitchapp.fragments.*
import com.example.hitchapp.fragments.HomeFragment.Companion.REQUEST_CODE_LOCATION
import com.example.hitchapp.models.User
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.ParseInstallation
import com.parse.ParseUser


class MainActivity : AppCompatActivity() {
    private var bottomNavigationView: BottomNavigationView? = null
    private val fragmentManager = supportFragmentManager
    private var appBarLayout: AppBarLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appBarLayout = findViewById(R.id.appBar)

        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground()

        // Find the toolbar view inside the activity layout
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar)
        supportActionBar?.elevation = 0f
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView?.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            val fragment: Fragment
            fragment = when (menuItem.itemId) {
                R.id.action_home -> HomeFragment()
                R.id.action_my_rides -> MyRidesFragment()
                R.id.action_post -> {
                    val user = ParseUser.getCurrentUser() as User
                    if (user.isDriver) ComposeFragment() else {
                        Toast.makeText(this@MainActivity, "You need to setup a driver profile to post a ride", Toast.LENGTH_SHORT).show()
                        MyProfileFragment()
                    }
                }
                R.id.action_profile -> MyProfileFragment()
                else -> MyProfileFragment()
            }

            //val ft: FragmentTransaction = fragmentManager.beginTransaction()
            //ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
            //ft.replace(R.id.flContainer, fragment).addToBackStack(TAG).commit()
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(TAG).commit()
            true
        })

        // Set default selection
        bottomNavigationView?.selectedItemId = R.id.action_home
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionsResult")
        if (requestCode == REQUEST_CODE_LOCATION) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Location permission request.")

            // Check if the only required permission has been granted
            if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "Location permission has now been granted.")
                //(fragmentManager?.findFragmentByTag("HomeFragment") as HomeFragment).mHomeFragmentViewModel?.queryRides()
            }
        }  else {
            super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        }
    }


    // Menu icons are inflated just as they were with actionbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        val fragment: Fragment
        fragment = when (item.itemId) {
            R.id.miRequests -> RequestsFragment()
            R.id.miSearch -> SearchFragment()
            else -> HomeFragment()
        }
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(TAG).commit()
        return true
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    fun setToolbar(expanded: Boolean){
        appBarLayout?.setExpanded(expanded, true);
    }
}
package com.example.hitchapp.viewmodels

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hitchapp.fragments.ComposeFragment
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.example.hitchapp.repositories.RideRepository
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.parse.ParseUser
import com.parse.SaveCallback
import org.json.JSONArray

class ComposeFragmentViewModel : ViewModel(){

    // Gets the person who's logged in
    private val currentUser = ParseUser.getCurrentUser() as User
    protected var mRepo: RideRepository? = null
    private val REQUEST_CODE = 20

    fun init() {
        mRepo = RideRepository.getInstance()
    }

    // Adds every aspect of the ride to a new Ride object and calls saveRide repo
    fun saveRide(from: String, to: String, price: String, departureDate: String, departureTime: String, saveRideCallback: SaveCallback) {

        // Sets ride to be everything that was set by user
        val ride = Ride()
        ride.price = price.toInt()
        ride.from = from
        ride.to = to
        ride.driver = currentUser
        ride.departureDate = departureDate
        ride.departureTime = departureTime
        if (ride.participants == null) {
            ride.participants = JSONArray()
        }
        var participants = ride.participants
        participants.put(currentUser);
        ride.participants = participants

        // Calls repo to method to save ride
        mRepo?.saveRide(ride, saveRideCallback)
    }


    companion object {
        const val TAG = "ComposeFragmentViewMode"
    }
}

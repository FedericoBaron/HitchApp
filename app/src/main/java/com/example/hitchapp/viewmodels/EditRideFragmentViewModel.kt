package com.example.hitchapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.example.hitchapp.repositories.RideRepository
import com.parse.ParseGeoPoint
import com.parse.ParseUser
import com.parse.SaveCallback
import org.json.JSONArray
import java.util.*

class EditRideFragmentViewModel: ViewModel(){

    // Gets the person who's logged in
    private val currentUser = ParseUser.getCurrentUser() as User
    protected var mRepo: RideRepository? = null

    fun init() {
        mRepo = RideRepository.instance
    }

    // Adds every aspect of the ride to a new Ride object and calls saveRide repo
    fun saveRide(ride: Ride, from: String, to: String, price: String, departureDate: Date?,
                 departureTime: String, fromLocation: ParseGeoPoint?, isPerPerson: Boolean, seatsAvailable: String, saveRideCallback: SaveCallback) {

        // Sets ride to be everything that was set by user
        ride.price = price.toInt()
        ride.from = from
        ride.to = to
        ride.driver = currentUser
        ride.departureDate = departureDate
        ride.departureTime = departureTime
        ride.fromLocation = fromLocation
        ride.pricePerParticipant = isPerPerson
        ride.seatsAvailable = seatsAvailable.toInt()
        if (ride.participants == null) {
            ride.participants = JSONArray()
        }

        Log.i(TAG, "saveeee")
        // Calls repo to method to save ride
        mRepo?.saveRide(ride, saveRideCallback)
    }


    companion object {
        const val TAG = "EditRideFragmentViewMod"
    }
}
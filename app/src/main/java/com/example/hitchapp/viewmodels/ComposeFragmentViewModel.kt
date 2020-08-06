package com.example.hitchapp.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.hitchapp.activities.SignupActivity
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.example.hitchapp.repositories.RideRepository
import com.parse.ParseGeoPoint
import com.parse.ParsePush
import com.parse.ParseUser
import com.parse.SaveCallback
import org.json.JSONArray
import java.util.*

class ComposeFragmentViewModel : ViewModel(){

    // Gets the person who's logged in
    private val currentUser = ParseUser.getCurrentUser() as User
    protected var mRepo: RideRepository? = null
    private val REQUEST_CODE = 20

    fun init() {
        mRepo = RideRepository.instance
    }

    // Adds every aspect of the ride to a new Ride object and calls saveRide repo
    fun saveRide(from: String, to: String, price: String, departureDate: Date?, departureTime: String, fromLocation: ParseGeoPoint?,
                 pricePerParticipant: Boolean, seatsAvailable: String, saveRideCallback: SaveCallback) {

        // Sets ride to be everything that was set by user
        val ride = Ride()
        ride.price = price.toInt()
        ride.from = from
        ride.to = to
        ride.driver = currentUser
        ride.departureDate = departureDate
        ride.departureTime = departureTime
        ride.fromLocation = fromLocation
        ride.pricePerParticipant = pricePerParticipant
        ride.seatsAvailable = seatsAvailable.toInt()
        if (ride.participants == null) {
            ride.participants = JSONArray()
        }
        var participants = ride.participants
        participants?.put(currentUser)
        ride.participants = participants

        val saveRideCallback = SaveCallback {e ->
            if (e == null) {
                ParsePush.subscribeInBackground(ride.objectId.toString())
            }
            else{
                Log.e(TAG, "Error while saving", e)
            }
        }

        // Calls repo to method to save ride
        mRepo?.saveRide(ride, saveRideCallback)

    }


    companion object {
        const val TAG = "ComposeFragmentViewMode"
    }
}

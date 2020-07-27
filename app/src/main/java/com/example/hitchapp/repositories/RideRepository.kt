package com.example.hitchapp.repositories

import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.SaveCallback

class RideRepository {
    // Gets rides
    fun ridesQuery(rides: Int, findCallback: FindCallback<Ride>?) {
        val query = ParseQuery.getQuery(Ride::class.java)
        query.include(Ride.KEY_DRIVER)
        var currentUser: User = ParseUser.getCurrentUser() as User
        query.whereWithinMiles(Ride.KEY_FROM_LOCATION, currentUser.currentLocation, 100.0)

        // Set a limit
        query.limit = rides

        // Sort by created at
        query.addDescendingOrder("createdAt")

        // Finds the posts asynchronously
        query.findInBackground(findCallback)
    }

    // Gets rides
    fun myRidesQuery(rides: Int, findCallback: FindCallback<Ride>?) {
        val query = ParseQuery.getQuery(Ride::class.java)
        val currentUser = ParseUser.getCurrentUser() as User
        query.whereEqualTo("participants", currentUser)
        query.include("participants")
        query.include("driver")

        // Set a limit
        query.limit = rides

        // Sort by created at
        query.addDescendingOrder("createdAt")

        // Finds the posts asynchronously
        query.findInBackground(findCallback)
    }

    // Saves the ride
    fun saveRide(ride: Ride, saveCallback: SaveCallback?) {
        ride.saveInBackground(saveCallback)
    }

    companion object {
        const val TAG = "RideRepository"
        @JvmStatic
        var instance: RideRepository? = null
            get() {
                if (field == null) {
                    field = RideRepository()
                }
                return field
            }
            private set
    }
}
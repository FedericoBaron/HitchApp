package com.example.hitchapp.repositories

import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.parse.*

class RideRepository {
    // Gets rides
    fun ridesQuery(rides: Int, findCallback: FindCallback<Ride>?) {
        val query = ParseQuery.getQuery(Ride::class.java)
        query.include(Ride.KEY_DRIVER)
        var currentUser: User = ParseUser.getCurrentUser() as User

        // If there's no more available seats then don't show since it's full
        query.whereNotEqualTo(Ride.KEY_SEATS_AVAILABLE, 0)

        // get rides within 20 miles
        query.whereWithinMiles(Ride.KEY_FROM_LOCATION, currentUser.currentLocation, 20.0)


        // Get only rides that are scheduled
        query.whereEqualTo(Ride.KEY_STATE, "Scheduled")

        // Set a limit
        query.limit = rides

        // Sort by departure date
        query.addAscendingOrder(Ride.KEY_DEPARTURE_DATE)

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
        query.addAscendingOrder(Ride.KEY_DEPARTURE_DATE)

        // Finds the posts asynchronously
        query.findInBackground(findCallback)
    }

    // Saves the ride
    fun saveRide(ride: Ride, saveCallback: SaveCallback?) {
        ride.saveInBackground(saveCallback)
    }

    // Deletes the ride
    fun deleteRide(ride: Ride, deleteCallback: DeleteCallback) {
        ride.deleteInBackground(deleteCallback)
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
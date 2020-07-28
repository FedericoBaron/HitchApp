package com.example.hitchapp.viewmodels

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hitchapp.activities.MainActivity
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.example.hitchapp.repositories.RideRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.parse.FindCallback
import com.parse.ParseGeoPoint
import com.parse.ParseUser
import java.util.*

class HomeFragmentViewModel : ViewModel() {
    private var mRides: MutableLiveData<List<Ride>>? = null
    private var mRepo: RideRepository? = null
    private var totalRides = 5
    private var mCurrentLocation: Location? = null
    private val currentUser = ParseUser.getCurrentUser() as User

    fun init() {
        if (mRides != null) {
            return
        }
        mRepo = RideRepository.instance
        queryRides()
    }

    val rides: LiveData<List<Ride>>
        get() {
            if (mRides == null) {
                mRides = MutableLiveData()
            }
            return mRides as MutableLiveData<List<Ride>>
        }

    // Query rides from repo
    fun queryRides() {

        val findCallback = FindCallback<Ride>{ rides, e ->
            if(e == null){
                for(i in 0 until rides.size) {
                    if (rides[i].departureDate?.compareTo(Calendar.getInstance().time as Date)!! < 0) {
                        rides[i].state = "Finished"
                        rides[i].save()
                        Log.i(TAG, rides[i].departureDate.toString())
                    }
                }
                Log.i(TAG, rides.toString())
                mRides?.postValue(rides)
            }
            else{
                Log.i(TAG, "Error querying for rides")
            }
        }

        mRepo?.ridesQuery(totalRides, findCallback)
    }

    // Loads more rides when we reach the bottom of TL
    fun loadMoreData() {
        // Adds more rides to the amount of rides queried in the repository
        totalRides += NEW_RIDES

        // Query rides from repo
        queryRides()
    }

    @SuppressWarnings("MissingPermission")
    //@NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getMyLocation(locationClient: FusedLocationProviderClient?) {
        Log.i(TAG, "THIS IS THE LOCATION")
        locationClient?.lastLocation
                ?.addOnSuccessListener { location ->
                    onLocationChanged(location)
                }
                ?.addOnFailureListener { e ->
                    Log.e(TAG, "Exception", e)
                }
    }

    private fun onLocationChanged(location: Location?) {
        Log.i(TAG, "location is this")
        // GPS may be turned off
        if (location == null) {
            Log.i(TAG, "location is NULL")
            return
        }

        // Report to the UI that the location was updated
        mCurrentLocation = location
        Log.i(TAG, "THE FINAL LIOCATION OS" + mCurrentLocation?.latitude.toString())
        Log.i(TAG, "THE FINAL LIOCATION OS" + mCurrentLocation?.longitude.toString())

        var geoPoint = ParseGeoPoint(location.latitude, location.longitude)

        currentUser.currentLocation = geoPoint
        currentUser.saveInBackground()
    }

    companion object {
        const val TAG = "HomeFragmentViewModel"
        protected const val NEW_RIDES = 5
    }
}
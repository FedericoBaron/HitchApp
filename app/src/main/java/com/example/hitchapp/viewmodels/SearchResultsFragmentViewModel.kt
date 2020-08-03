package com.example.hitchapp.viewmodels

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.example.hitchapp.repositories.RideRepository
import com.parse.FindCallback
import com.parse.ParseGeoPoint
import com.parse.ParseUser
import com.parse.SaveCallback
import java.util.*

class SearchResultsFragmentViewModel: ViewModel() {

    private var from: ParseGeoPoint? = null
    private var distance: Int? = null
    protected var allRides: MutableLiveData<List<Ride>>? = null
    protected var repo: RideRepository? = null
    protected var totalRides = 5

    fun init(from: ParseGeoPoint, distance: Int) {
        if (allRides != null) {
            return
        }
        repo = RideRepository.instance
        this.from = from
        this.distance = distance
        queryRides()
    }

    val rides: LiveData<List<Ride>>
        get() {
            if (allRides == null) {
                allRides = MutableLiveData()
            }
            return allRides as MutableLiveData<List<Ride>>
        }

    // Query rides from repo
    fun queryRides() {

        val findCallback = FindCallback<Ride>{ rides, e ->
            if(e == null){
                for(i in 0 until rides.size) {
                    if (rides[i].departureDate?.compareTo(Calendar.getInstance().time as Date)!! < 0) {
                        rides[i].state = "Finished"
                        save(rides[i])
                        Log.i(TAG, rides[i].departureDate.toString())
                    }
                }
                Log.i(TAG, rides.toString())
                allRides?.postValue(rides)
            }
            else{
                Log.i(TAG, "Error querying for rides", e)
            }
        }
        distance?.let { from?.let { it1 -> repo?.searchRidesQuery(it1, it, totalRides, findCallback) } }
    }

    fun save(ride: Ride){
        val saveCallback = SaveCallback{ e ->
            if(e == null){
                Log.i(MyRidesFragmentViewModel.TAG, "saved ride")
            }
            else{
                Log.e(MyRidesFragmentViewModel.TAG, "Issue with save", e)
            }
        }

        repo?.saveRide(ride, saveCallback)
    }

    // Loads more rides when we reach the bottom of TL
    fun loadMoreData() {
        // Adds more rides to the amount of rides queried in the repository
        totalRides += NEW_RIDES

        // Query rides from repo
        queryRides()
    }


    companion object {
        private const val TAG = "SearchResultsFragmentVi"
        protected const val NEW_RIDES = 5

    }

}

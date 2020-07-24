package com.example.hitchapp.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hitchapp.activities.LoginActivity
import com.example.hitchapp.models.Ride
import com.example.hitchapp.repositories.RideRepository
import com.parse.FindCallback

class HomeFragmentViewModel : ViewModel() {
    protected var mRides: MutableLiveData<List<Ride>>? = null
    protected var mRepo: RideRepository? = null
    protected var totalRides = 5

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

    companion object {
        const val TAG = "HomeFragmentViewModel"
        protected const val NEW_RIDES = 5
    }
}
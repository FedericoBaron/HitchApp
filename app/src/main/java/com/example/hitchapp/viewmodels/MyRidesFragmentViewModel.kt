package com.example.hitchapp.viewmodels

import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hitchapp.activities.LoginActivity
import com.example.hitchapp.models.Ride
import com.example.hitchapp.repositories.RideRepository
import com.example.hitchapp.repositories.RideRepository.Companion.instance
import com.parse.DeleteCallback
import com.parse.FindCallback
import com.parse.SaveCallback
import java.util.*

class MyRidesFragmentViewModel : ViewModel() {
    protected var mRides: MutableLiveData<List<Ride>>? = null
    protected var mRepo: RideRepository? = null
    protected var totalRides = 5
    fun init() {
        if (mRides != null) {
            return
        }
        mRepo = instance
        queryMyRides()
    }

    val rides: LiveData<List<Ride>>
        get() {
            if (mRides == null) {
                mRides = MutableLiveData()
            }
            return mRides as MutableLiveData<List<Ride>>
        }

    // Query rides from repo
    fun queryMyRides() {
        val findCallback = FindCallback<Ride>{ rides, e ->
            if(e == null){
                for(i in 0 until rides.size) {
                    if (rides[i].state == "Scheduled" && rides[i].departureDate?.compareTo(Calendar.getInstance().time as Date)!! < 0) {
                        rides[i].state = "Finished"
                        rides[i].save()
                    }
                    else if(rides[i].state == "Finished" && rides[i].departureDate?.compareTo(Calendar.getInstance().time as Date)!! >= 0){
                        rides[i].state = "Scheduled"
                        rides[i].save()
                    }
                }
                mRides?.postValue(rides)
            }
            else{
                Log.i(HomeFragmentViewModel.TAG, "Error querying for rides")
            }
        }
        mRepo?.myRidesQuery(totalRides, findCallback)
    }

    // Loads more rides when we reach the bottom of TL
    fun loadMoreData() {
        Log.i(TAG, "load more data")
        // Adds more rides to the amount of rides queried in the repository
        totalRides += NEW_RIDES

        // Query rides from repo
        queryMyRides()
    }

    fun save(ride: Ride){
        val saveCallback = SaveCallback{ e ->
            if(e == null){
                Log.i(TAG, "saved ride")
            }
            else{
                Log.e(TAG, "Issue with save", e)
            }
        }

        mRepo?.saveRide(ride, saveCallback)
    }

    fun delete(ride: Ride){
        val deleteCallback = DeleteCallback{ e ->
            if(e == null){
                Log.i(TAG, "deleted ride")
            }
            else{
                Log.e(TAG, "Issue with delete", e)
            }
        }

        mRepo?.deleteRide(ride, deleteCallback)
    }


    companion object {
        const val TAG = "MyRidesFragmentViewMode"
        protected const val NEW_RIDES = 5
    }
}
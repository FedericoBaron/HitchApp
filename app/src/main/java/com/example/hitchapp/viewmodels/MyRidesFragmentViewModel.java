package com.example.hitchapp.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hitchapp.models.Ride;
import com.example.hitchapp.repositories.RideRepository;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public class MyRidesFragmentViewModel extends ViewModel{

    public static final String TAG = "MyRidesFragmentViewMode";

    protected MutableLiveData<List<Ride>> mRides;
    protected RideRepository mRepo;
    protected int totalRides = 5;
    protected static final int NEW_RIDES = 5;

    public void init(){
        if(mRides != null){
            return;
        }

        mRepo = RideRepository.getInstance();

        queryMyRides();

    }

    public LiveData<List<Ride>> getRides(){
        if(mRides == null){
            mRides = new MutableLiveData<>();
        }
        return (MutableLiveData<List<Ride>>) mRides;
    }

    // Query rides from repo
    public void queryMyRides(){
        mRepo.myRidesQuery(totalRides, new FindCallback<Ride>() {
            @Override
            public void done(List<Ride> objects, ParseException e) {
                Log.i(TAG, objects.toString());
                mRides.postValue(objects);
            }
        });
    }

    // Loads more rides when we reach the bottom of TL
    public void loadMoreData() {
        Log.i(TAG, "load more data");
        // Adds more rides to the amount of rides queried in the repository
        totalRides = totalRides + NEW_RIDES;

        // Query rides from repo
        queryMyRides();
    }
}

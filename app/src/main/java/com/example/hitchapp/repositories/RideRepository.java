package com.example.hitchapp.repositories;

import android.util.Log;

import com.example.hitchapp.models.Ride;
import com.example.hitchapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class RideRepository {

    public static final String TAG = "RideRepository";
    private static RideRepository instance;

    public static RideRepository getInstance(){
        if(instance == null){
            instance = new RideRepository();
        }
        return instance;
    }

    // Gets rides
    public void ridesQuery(int rides, FindCallback<Ride> findCallback){
        ParseQuery<Ride> query = ParseQuery.getQuery(Ride.class);
        query.include(Ride.KEY_DRIVER);

        // Set a limit
        query.setLimit(rides);

        // Sort by created at
        query.addDescendingOrder(Ride.KEY_CREATED_AT);

        // Finds the posts asynchronously
        query.findInBackground(findCallback);
    }

    // Gets rides
    public void myRidesQuery(int rides, FindCallback<Ride> findCallback){
        ParseQuery<Ride> query = ParseQuery.getQuery(Ride.class);
        User currentUser = (User) ParseUser.getCurrentUser();
        query.whereEqualTo("participants", currentUser);
        query.include("participants");
        query.include("driver");

        // Set a limit
        query.setLimit(rides);

        // Sort by created at
        query.addDescendingOrder(Ride.KEY_CREATED_AT);

        // Finds the posts asynchronously
        query.findInBackground(findCallback);
    }


    // Saves the ride
    public void saveRide(Ride ride, SaveCallback saveCallback){
        ride.saveInBackground(saveCallback);
    }

}

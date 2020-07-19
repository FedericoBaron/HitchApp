package com.example.hitchapp.fragments;

import android.util.Log;

import com.example.hitchapp.fragments.HomeFragment;
import com.example.hitchapp.models.Ride;
import com.example.hitchapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.parceler.Parcels;

import java.util.List;

public class MyRidesFragment extends HomeFragment {

    private static final String TAG = "MyRidesFragment";
    private int totalPosts = 20;

    User currentUser = (User) ParseUser.getCurrentUser();

    @Override
    protected void queryRides() {
        ParseQuery<Ride> query = ParseQuery.getQuery(Ride.class);
        query.whereEqualTo("participants", currentUser);
        query.include("participants");
        query.include("driver");

        // Set a limit of 20 posts
        query.setLimit(totalPosts);

        // Sort by created at
        query.addDescendingOrder(Ride.KEY_CREATED_AT);

        // Finds the posts asynchronously
        query.findInBackground(new FindCallback<Ride>() {
            @Override
            public void done(List<Ride> rides, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting rides", e);
                    return;
                }
                for(Ride ride: rides){
                    //Log.i(TAG, "Ride: " + ride.getDriver().get + ", username: " + ride.getUser().getUsername());
                }

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add posts to adapter
                adapter.setAll(rides);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    // Loads more posts when we reach the bottom of TL
    protected void loadMoreData() {
        Log.i(TAG, "Loading more data");
        totalPosts = totalPosts + NEW_RIDES;
        ParseQuery<Ride> query = ParseQuery.getQuery(Ride.class);
        query.whereEqualTo("participants", currentUser);
        query.include("participants");
        query.include("driver");

        // Set a limit of 20 posts
        query.setLimit(totalPosts);

        // Sort by created at
        query.addDescendingOrder(Ride.KEY_CREATED_AT);

        // Finds the posts asynchronously
        query.findInBackground(new FindCallback<Ride>() {
            @Override
            public void done(List<Ride> rides, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting rides", e);
                    return;
                }
                for(Ride ride: rides){
                    //Log.i(TAG, "Ride: " + ride.getDriver().get + ", username: " + ride.getUser().getUsername());
                }

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add posts to adapter
                adapter.setAll(rides);
                adapter.notifyDataSetChanged();
            }
        });

    }
}
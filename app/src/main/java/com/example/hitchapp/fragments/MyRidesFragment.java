package com.example.hitchapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hitchapp.R;
import com.example.hitchapp.adapters.MyRidesAdapter;
import com.example.hitchapp.adapters.RidesAdapter;
import com.example.hitchapp.fragments.HomeFragment;
import com.example.hitchapp.models.Ride;
import com.example.hitchapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MyRidesFragment extends HomeFragment {

    private static final String TAG = "MyRidesFragment";
    private int totalPosts = 20;

    User currentUser = (User) ParseUser.getCurrentUser();
    private RecyclerView rvRides;
    public static MyRidesAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRides = view.findViewById(R.id.rvRides);
        pbLoading = view.findViewById(R.id.pbLoading);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvRides.addItemDecoration(itemDecoration);

        // Create layout for one row in the list
        // Create the adapter
        allRides = new ArrayList<>();
        adapter = new MyRidesAdapter(getContext(), allRides);

        // Set the adapter on the recycler view
        rvRides.setAdapter(adapter);

        // Set the layout manager on the recycler view
        rvRides.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutManager = (LinearLayoutManager) rvRides.getLayoutManager();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Listener for refreshing timeline
        refreshListener();

        createScrollListener();

        // Gets all the rides for the timeline
        queryRides();
    }

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
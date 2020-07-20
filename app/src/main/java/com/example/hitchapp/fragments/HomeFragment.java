package com.example.hitchapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hitchapp.EndlessRecyclerViewScrollListener;
import com.example.hitchapp.R;
import com.example.hitchapp.adapters.RidesAdapter;
import com.example.hitchapp.models.Ride;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private RecyclerView rvRides;
    public static RidesAdapter adapter;
    public static List<Ride> allRides;
    protected SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    protected LinearLayoutManager layoutManager;
    private int totalRides = 20;
    protected ProgressBar pbLoading;
    protected static final int NEW_RIDES = 5;
    private final int REQUEST_CODE = 20;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

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
        adapter = new RidesAdapter(getContext(), allRides);

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

    protected void createScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                loadMoreData();
            }
        };

        // Adds the scroll listener to the RV
        rvRides.addOnScrollListener(scrollListener);
    }

    // Loads more rides when we reach the bottom of TL
    protected void loadMoreData() {
        Log.i(TAG, "Loading more data");
        totalRides = totalRides + NEW_RIDES;
        ParseQuery<Ride> query = ParseQuery.getQuery(Ride.class);
        query.include(Ride.KEY_DRIVER);

        // Set a limit
        query.setLimit(totalRides);

        // Sort by created at
        query.addDescendingOrder(Ride.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Ride>() {
            @Override
            public void done(List<Ride> rides, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting rides", e);
                    return;
                }
                for(Ride ride : rides){
                    //Log.i(TAG, "Ride: " + ride.getDescription() + ", username: " + ride.getUser().getUsername());
                }

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add rides to adapter
                adapter.setAll(rides);
                adapter.notifyItemRangeInserted(rides.size()-5, rides.size());
            }
        });
    }


    protected void refreshListener(){
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryRides();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }


    // Gets rides and notifies adapter
    protected void queryRides(){

        pbLoading.setVisibility(ProgressBar.VISIBLE);

        ParseQuery<Ride> query = ParseQuery.getQuery(Ride.class);
        query.include(Ride.KEY_DRIVER);

        // Set a limit
        query.setLimit(totalRides);

        // Sort by created at
        query.addDescendingOrder(Ride.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Ride>() {
            @Override
            public void done(List<Ride> rides, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for(Ride ride : rides){
                    //Log.i(TAG, "Ride: " + ride.getDescription() + ", username: " + ride.getUser().getUsername());
                }
                // run a background job and once complete
                pbLoading.setVisibility(ProgressBar.INVISIBLE);

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add rides to adapter
                adapter.setAll(rides);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
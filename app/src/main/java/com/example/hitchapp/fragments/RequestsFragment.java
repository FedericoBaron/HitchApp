package com.example.hitchapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hitchapp.EndlessRecyclerViewScrollListener;
import com.example.hitchapp.R;
import com.example.hitchapp.adapters.RequestsAdapter;
import com.example.hitchapp.models.Request;
import com.example.hitchapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private RecyclerView rvRequests;
    public static RequestsAdapter adapter;
    public static List<Request> allRequests;
    protected SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    protected LinearLayoutManager layoutManager;
    private int totalRequests = 20;
    private User currentUser;
    protected ProgressBar pbLoading;
    protected static final int NEW_REQUESTS = 5;
    private final int REQUEST_CODE = 20;

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRequests = view.findViewById(R.id.rvRequests);
        pbLoading = view.findViewById(R.id.pbLoading);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvRequests.addItemDecoration(itemDecoration);

        // Create layout for one row in the list
        // Create the adapter
        allRequests = new ArrayList<>();
        adapter = new RequestsAdapter(getContext(), allRequests);

        // Current user
        User currentUser = (User) ParseUser.getCurrentUser();

        // Set the adapter on the recycler view
        rvRequests.setAdapter(adapter);

        // Set the layout manager on the recycler view
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutManager = (LinearLayoutManager) rvRequests.getLayoutManager();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Listener for refreshing timeline
        refreshListener();

        createScrollListener();

        // Gets all the rides for the timeline
        queryRequests();
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
        rvRequests.addOnScrollListener(scrollListener);
    }

    // Loads more rides when we reach the bottom of TL
    protected void loadMoreData() {
        Log.i(TAG, "Loading more data");
        totalRequests = totalRequests + NEW_REQUESTS;
        ParseQuery<Request> query = ParseQuery.getQuery(Request.class);
        query.include("ride");
        query.include("ride.driver");

        // Set a limit
        query.setLimit(totalRequests);

        // Sort by created at
        query.addDescendingOrder(Request.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> requests, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting rides", e);
                    return;
                }
                for(Request request : requests){
                    //Log.i(TAG, "Ride: " + ride.getDescription() + ", username: " + ride.getUser().getUsername());
                }

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add rides to adapter
                adapter.setAll(requests);
                adapter.notifyItemRangeInserted(requests.size()-5, requests.size());
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
                queryRequests();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }


    // Gets rides and notifies adapter
    protected void queryRequests(){

        pbLoading.setVisibility(ProgressBar.VISIBLE);

        ParseQuery<Request> query = ParseQuery.getQuery(Request.class);
        query.include("ride");
        query.include("ride.driver");
        query.whereEqualTo("ride.driver", currentUser);

        // Set a limit
        query.setLimit(totalRequests);

        // Sort by created at
        query.addDescendingOrder(Request.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> requests, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting requests", e);
                    return;
                }
                for(Request request : requests){
                    //Log.i(TAG, "Ride: " + ride.getDescription() + ", username: " + ride.getUser().getUsername());
                }
                // run a background job and once complete
                pbLoading.setVisibility(ProgressBar.INVISIBLE);

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add rides to adapter
                adapter.setAll(requests);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hitchapp.EndlessRecyclerViewScrollListener;
import com.example.hitchapp.R;
import com.example.hitchapp.adapters.MyRidesAdapter;
import com.example.hitchapp.models.Ride;
import com.example.hitchapp.models.User;
import com.example.hitchapp.viewmodels.MyRidesFragmentViewModel;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MyRidesFragment extends Fragment {

    private static final String TAG = "MyRidesFragment";

    protected ProgressBar pbLoading;
    private RecyclerView rvRides;
    public static MyRidesAdapter adapter;
    public static List<Ride> allRides;
    protected LinearLayoutManager layoutManager;
    private MyRidesFragmentViewModel myRidesFragmentViewModel;
    protected SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    public MyRidesFragment() {
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

        rvRides = view.findViewById(R.id.rvRides);
        pbLoading = view.findViewById(R.id.pbLoading);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvRides.addItemDecoration(itemDecoration);

        // gets list of rides from livedata
        allRides = new ArrayList<>();
        adapter = new MyRidesAdapter(getContext(), allRides);

        // Set the adapter on the recycler view
        rvRides.setAdapter(adapter);

        // Set the layout manager on the recycler view
        rvRides.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutManager = (LinearLayoutManager) rvRides.getLayoutManager();

        startViewModel();

        // Show progress bar loading
        pbLoading.setVisibility(View.VISIBLE);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Listener for refreshing timeline
        refreshListener();

        // Listens for when you need to load more data
        createScrollListener();
    }

    protected void startViewModel() {
        myRidesFragmentViewModel = ViewModelProviders.of(this).get(MyRidesFragmentViewModel.class);

        myRidesFragmentViewModel.init();

        // Create the observer which updates the UI.
        final Observer<List<Ride>> ridesObserver = new Observer<List<Ride>>() {
            @Override
            public void onChanged(@Nullable final List<Ride> rides) {
                // Update the UI
                for (Ride ride : rides) {
                    Log.i(TAG, "Ride: " + ride.getDriver() + ", username: " + ride.getMessages());
                }
                adapter.setAll(rides);
                adapter.notifyDataSetChanged();
                pbLoading.setVisibility(View.GONE);
                swipeContainer.setRefreshing(false);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        myRidesFragmentViewModel.getRides().observe(getViewLifecycleOwner(), ridesObserver);
    }

    protected void refreshListener() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                myRidesFragmentViewModel.queryMyRides();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Listens for when you need to load more data
    protected void createScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                myRidesFragmentViewModel.loadMoreData();
            }
        };
        // Adds the scroll listener to the RV
        rvRides.addOnScrollListener(scrollListener);
    }
}


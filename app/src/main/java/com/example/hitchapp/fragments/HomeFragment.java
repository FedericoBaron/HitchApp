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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hitchapp.EndlessRecyclerViewScrollListener;
import com.example.hitchapp.R;
import com.example.hitchapp.adapters.RidesAdapter;
import com.example.hitchapp.models.Ride;
import com.example.hitchapp.viewmodels.HomeFragmentViewModel;
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
    protected ProgressBar pbLoading;
    private HomeFragmentViewModel mHomeFragmentViewModel;

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

        startViewModel();

        // Show progress bar loading
        pbLoading.setVisibility(View.VISIBLE);

        initRecyclerView();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Listener for refreshing timeline
        refreshListener();

        // Listens for when you need to load more data
        createScrollListener();
    }

    protected void startViewModel(){
        mHomeFragmentViewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel.class);

        mHomeFragmentViewModel.init();

        // Create the observer which updates the UI.
        final Observer<List<Ride>> ridesObserver = new Observer<List<Ride>>() {
            @Override
            public void onChanged(@Nullable final List<Ride> rides) {
                // Update the UI
                for(Ride ride: rides){
                    Log.i(TAG, "Ride: " + ride.getDriver() + ", username: " + ride.getMessages());
                }
                adapter.setAll(rides);
                adapter.notifyDataSetChanged();
                pbLoading.setVisibility(View.GONE);
                swipeContainer.setRefreshing(false);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mHomeFragmentViewModel.getRides().observe(getViewLifecycleOwner(), ridesObserver);
    }

    protected void initRecyclerView(){

        // gets list of rides from livedata
        allRides = new ArrayList<>();
        adapter = new RidesAdapter(getContext(), allRides);

        // Set the adapter on the recycler view
        rvRides.setAdapter(adapter);

        // Set the layout manager on the recycler view
        rvRides.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutManager = (LinearLayoutManager) rvRides.getLayoutManager();
    }


    protected void refreshListener(){
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                mHomeFragmentViewModel.queryRides();
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
                mHomeFragmentViewModel.loadMoreData();
            }
        };

        // Adds the scroll listener to the RV
        rvRides.addOnScrollListener(scrollListener);
    }
}
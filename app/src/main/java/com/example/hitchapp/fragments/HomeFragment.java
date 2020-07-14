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
import com.example.hitchapp.adapters.PostsAdapter;
import com.example.hitchapp.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private RecyclerView rvPosts;
    public static PostsAdapter adapter;
    public static List<Post> allPosts;
    protected SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager layoutManager;
    private int totalPosts = 20;
    private ProgressBar pbLoading;
    protected static final int NEW_POSTS = 5;
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

        rvPosts = view.findViewById(R.id.rvPosts);
        pbLoading = view.findViewById(R.id.pbLoading);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvPosts.addItemDecoration(itemDecoration);

        // Create layout for one row in the list
        // Create the adapter
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);

        // Set the adapter on the recycler view
        rvPosts.setAdapter(adapter);

        // Set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutManager = (LinearLayoutManager) rvPosts.getLayoutManager();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Listener for refreshing timeline
        refreshListener();

        createScrollListener();

        // Gets all the posts for the timeline
        queryPosts();
    }

    private void createScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                loadMoreData();
            }
        };

        // Adds the scroll listener to the RV
        rvPosts.addOnScrollListener(scrollListener);
    }

    // Loads more posts when we reach the bottom of TL
    protected void loadMoreData() {
        Log.i(TAG, "Loading more data");
        totalPosts = totalPosts + NEW_POSTS;
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_DRIVER);

        // Set a limit
        query.setLimit(totalPosts);

        // Sort by created at
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for(Post post: posts){
                    //Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add posts to adapter
                adapter.setAll(posts);
                adapter.notifyItemRangeInserted(posts.size()-5, posts.size());
            }
        });
    }


    private void refreshListener(){
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPosts();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }


    // Gets posts and notifies adapter
    protected void queryPosts(){

        pbLoading.setVisibility(ProgressBar.VISIBLE);

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_DRIVER);

        // Set a limit
        query.setLimit(totalPosts);

        // Sort by created at
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for(Post post: posts){
                    //Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                // run a background job and once complete
                pbLoading.setVisibility(ProgressBar.INVISIBLE);

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add posts to adapter
                adapter.setAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
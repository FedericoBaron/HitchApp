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
import com.example.hitchapp.adapters.ConversationsAdapter;
import com.example.hitchapp.models.Conversation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ConversationsFragment extends Fragment {

    private static final String TAG = "ConversationsFragment";

    private RecyclerView rvConversations;
    public static ConversationsAdapter adapter;
    public static List<Conversation> allConversations;
    protected SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager layoutManager;
    private int totalConversations = 20;
    private ProgressBar pbLoading;
    protected static final int NEW_CONVERSATIONS = 5;
    private final int REQUEST_CODE = 20;

    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvConversations = view.findViewById(R.id.rvConversations);
        pbLoading = view.findViewById(R.id.pbLoading);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvConversations.addItemDecoration(itemDecoration);

        // Create layout for one row in the list
        // Create the adapter
        allConversations = new ArrayList<>();
        adapter = new ConversationsAdapter(getContext(), allConversations);

        // Set the adapter on the recycler view
        rvConversations.setAdapter(adapter);

        // Set the layout manager on the recycler view
        rvConversations.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutManager = (LinearLayoutManager) rvConversations.getLayoutManager();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Listener for refreshing timeline
        refreshListener();

        createScrollListener();

        // Gets all the conversations for the timeline
        queryConversations();
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
        rvConversations.addOnScrollListener(scrollListener);
    }

    // Loads more conversations when we reach the bottom of TL
    protected void loadMoreData() {
        Log.i(TAG, "Loading more data");
        totalConversations = totalConversations + NEW_CONVERSATIONS;

        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);

        //query.include

        // Set a limit
        query.setLimit(totalConversations);

        // Sort by updated at
        query.addDescendingOrder(Conversation.KEY_UPDATED_AT);
        query.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> conversations, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for(Conversation conversation: conversations){
                    Log.i(TAG, "Conversation: " + conversation.getGroupName());
                }

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add posts to adapter
                adapter.setAll(conversations);
                //adapter.notifyItemRangeInserted(conversations.size()-5, conversations.size());
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
                queryConversations();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }


    // Gets posts and notifies adapter
    protected void queryConversations(){

        pbLoading.setVisibility(ProgressBar.VISIBLE);

        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        //query.include(Post.KEY_DRIVER);

        // Set a limit
        query.setLimit(totalConversations);

        // Sort by updated at
        query.addDescendingOrder(Conversation.KEY_UPDATED_AT);

        query.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> conversations, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for(Conversation conversation: conversations){
                    Log.i(TAG, "Conversation: " + conversation.getGroupName());
                }
                // run a background job and once complete
                pbLoading.setVisibility(ProgressBar.INVISIBLE);

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add posts to adapter
                adapter.setAll(conversations);
            }
        });
    }

}
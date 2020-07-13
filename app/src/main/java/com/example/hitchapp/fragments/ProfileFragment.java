package com.example.hitchapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hitchapp.R;
import com.example.hitchapp.activities.LoginActivity;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private Button btnLogout;
    private View view;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        // Connects frontend to backend
        wireUI();

        // Listens for logout button click
        logoutListener();
    }

    // Sets variables to views
    private void wireUI(){
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void logoutListener(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null

                // Go to login screen
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);

                // Prevent people from going back after logging out
                getActivity().finish();
            }
        });
    }
}

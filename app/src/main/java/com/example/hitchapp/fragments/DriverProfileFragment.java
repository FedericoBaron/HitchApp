package com.example.hitchapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hitchapp.R;
import com.example.hitchapp.activities.LoginActivity;
import com.example.hitchapp.models.Car;
import com.example.hitchapp.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;

public class DriverProfileFragment extends Fragment {
    private static final String TAG = "DriverProfileFragment";

    private User driver;
    private Car car;
    private View view;

    // User stuff
    private ImageView ivProfilePicture;
    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvCollege;
    private TextView tvBiography;

    // Car stuff
    private TextView tvCarModel;
    private TextView tvCarMaker;
    private TextView tvCarYear;

    public DriverProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        // Unwrap the user passed in via bundle, using its simple name as a key
        //driver = Parcels.unwrap(getArguments().getParcelable("user"));
        Bundle bundle = this.getArguments();
        driver = (User) bundle.getParcelable("user");

        // Connects frontend to backend
        wireUI();

        // Sets up the profile
        setProfileInfo();
    }

    // Sets variables to views
    private void wireUI(){

        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvLastName = view.findViewById(R.id.tvLastName);
        tvCollege = view.findViewById(R.id.tvCollege);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvBiography = view.findViewById(R.id.tvBiography);

        // Driver Profile
        tvCarMaker = view.findViewById(R.id.tvCarMaker);
        tvCarModel = view.findViewById(R.id.tvCarModel);
        tvCarYear = view.findViewById(R.id.tvCarYear);
    }

    private void setProfileInfo(){
        // Sets info to match that user
        Log.i(TAG, driver.getCollege());
        tvCollege.setText(driver.getCollege());
        tvFirstName.setText(driver.getFirstName());
        tvLastName.setText(driver.getLastName());
        tvBiography.setText(driver.getBiography());
        ParseFile image = driver.getProfilePicture();
        if(image != null) {
            Log.i(TAG, "here" + image);
            Glide.with(getContext())
                    .load(image.getUrl())
                    .fitCenter()
                    .circleCrop()
                    .into(ivProfilePicture);
        }

        car = driver.getCar();

        // fetch car data
        try {
            car.fetch();
        } catch(Exception e){
            Log.e(TAG, "Couldn't fetch car", e);
        }

        tvCarModel.setText(car.getCarModel());
        tvCarMaker.setText(car.getCarMaker());
        tvCarYear.setText(String.valueOf(car.getCarYear()));
        }

}

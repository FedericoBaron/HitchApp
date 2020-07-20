package com.example.hitchapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hitchapp.R;
import com.example.hitchapp.models.Car;
import com.example.hitchapp.models.User;
import com.parse.ParseFile;

public class ProfileFragment extends Fragment {
    private static final String TAG = "DriverProfileFragment";

    private User user;
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
    private TextView tvCarInfo;

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

        // Unwrap the user passed in via bundle, using its simple name as a key
        //driver = Parcels.unwrap(getArguments().getParcelable("user"));
        Bundle bundle = this.getArguments();
        user = (User) bundle.getParcelable("user");

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
        tvCarInfo = view.findViewById(R.id.tvCarInfo);
    }

    private void setProfileInfo(){
        // Sets info to match that user
        Log.i(TAG, user.getCollege());
        tvCollege.setText(user.getCollege());
        tvFirstName.setText(user.getFirstName());
        tvLastName.setText(user.getLastName());
        tvBiography.setText(user.getBiography());
        ParseFile image = user.getProfilePicture();
        if(image != null) {
            Log.i(TAG, "here" + image);
            Glide.with(getContext())
                    .load(image.getUrl())
                    .fitCenter()
                    .circleCrop()
                    .into(ivProfilePicture);
        }
        if(user.getCar() == null){
            tvCarMaker.setVisibility(View.GONE);
            tvCarModel.setVisibility(View.GONE);
            tvCarYear.setVisibility(View.GONE);
            tvCarInfo.setVisibility(View.GONE);
        }
        else{
            tvCarMaker.setVisibility(View.VISIBLE);
            tvCarModel.setVisibility(View.VISIBLE);
            tvCarYear.setVisibility(View.VISIBLE);
            tvCarInfo.setVisibility(View.VISIBLE);

            car = user.getCar();

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

}
